package com.enderio.conduits.common.conduit.network;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.connection.config.IOConnectionConfig;
import com.enderio.conduits.api.network.ConduitBlockConnection;
import com.enderio.conduits.api.network.ConduitNetworkContext;
import com.enderio.conduits.api.network.ConduitNetworkContextType;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.conduits.api.network.node.IConduitNode;
import com.enderio.core.common.graph.Network;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.CrashReportCategory;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class ConduitNetwork extends Network<ConduitNetwork, ConduitNode> implements IConduitNetwork {

    public static final Codec<ConduitNetwork> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Conduit.CODEC.fieldOf("conduit").forGetter(i -> i.conduit),
                    ConduitNetworkContext.GENERIC_CODEC.optionalFieldOf("context")
                            .forGetter(i -> i.context == null || !i.context.type().isPersistent() ? Optional.empty()
                                    : Optional.of(i.context)))
            .and(graphCodec(instance, ConduitNode.CODEC))
            .apply(instance, ConduitNetwork::new));

    private final Holder<Conduit<?, ?>> conduit;

    @Nullable
    private ConduitNetworkContext<?> context;

    // Caches
    private final boolean supportsCaching;

    private boolean shouldRebuildCache = true;
    private boolean haveConnectionsChanged = true;
    private final Set<ConduitNode> dirtyNodes = Sets.newHashSet();

    private final Multimap<Long, ConduitNode> nodesByChunkPos = HashMultimap.create();

    private final Set<ConduitNode> tickingNodes = Sets.newHashSet();

    // TODO: Separate this into a list and a multimap so we can sort all endpointConnections?
    private final SetMultimap<ConduitNode, ConduitBlockConnection> endpointConnections = HashMultimap.create();
    private final Map<ConduitBlockConnection, List<ConduitBlockConnection>> accessibleBlockConnectionsMap = Maps
            .newHashMap();

    private final List<ConduitBlockConnection> insertConnections = Lists.newArrayList();
    private final List<ConduitBlockConnection> extractConnections = Lists.newArrayList();

    private final Set<DyeColor> allChannels = Sets.newHashSet();
    private final ListMultimap<DyeColor, ConduitBlockConnection> insertConnectionsByChannel = ArrayListMultimap
            .create();
    private final ListMultimap<DyeColor, ConduitBlockConnection> extractConnectionsByChannel = ArrayListMultimap
            .create();

    private final Map<ConduitBlockConnection, List<ConduitBlockConnection>> extractConnectionsByInsert = Maps
            .newHashMap();
    private final Map<ConduitBlockConnection, List<ConduitBlockConnection>> insertConnectionsByExtract = Maps
            .newHashMap();

    @Nullable
    private Consumer<ConduitNetwork> onChunkCoverageChanged = null;

    public ConduitNetwork(Holder<Conduit<?, ?>> conduit, ConduitNode initialNode) {
        super(initialNode);
        this.conduit = conduit;
        this.supportsCaching = conduit.value().ticker() != null;
    }

    // TODO: Only public for legacy deserialisation.
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ConduitNetwork(Holder<Conduit<?, ?>> conduit, Optional<ConduitNetworkContext<?>> context,
            List<ConduitNode> nodes, IndexedEdgeList edges) {
        super(nodes, edges);
        this.conduit = conduit;
        this.context = context.orElse(null);
        this.supportsCaching = conduit.value().ticker() != null;
    }

    protected ConduitNetwork(Holder<Conduit<?, ?>> conduit) {
        this.conduit = conduit;
        this.supportsCaching = conduit.value().ticker() != null;
    }

    public Holder<Conduit<?, ?>> conduit() {
        return conduit;
    }

    // region Chunk Tracking

    public Set<Long> allChunks() {
        return nodesByChunkPos.keySet();
    }

    public void setOnChunkCoverageChanged(Consumer<ConduitNetwork> onChunkCoverageChanged) {
        this.onChunkCoverageChanged = onChunkCoverageChanged;
    }

    // endregion

    // region Queries

    // These are unfortunately necessary for the IConduitNetwork interface.
    @Override
    public boolean contains(IConduitNode node) {
        if (node instanceof ConduitNode typedNode) {
            return contains(typedNode);
        }

        return false;
    }

    @Override
    public Set<? extends IConduitNode> neighbors(IConduitNode node) {
        if (node instanceof ConduitNode typedNode) {
            return neighbors(typedNode);
        }

        return Set.of();
    }

    public Collection<ConduitNode> tickingNodes() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return Collections.unmodifiableCollection(tickingNodes);
    }

    public Collection<ConduitNode> blockEndpoints() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return Collections.unmodifiableCollection(endpointConnections.keySet());
    }

    public Collection<ConduitBlockConnection> blockConnections() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return Collections.unmodifiableCollection(endpointConnections.values());
    }

    // This is sorted
    public List<ConduitBlockConnection> blockConnectionsAccessibleFrom(ConduitBlockConnection connection) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return accessibleBlockConnectionsMap.getOrDefault(connection, List.of());
    }

    public Set<DyeColor> allChannels() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return allChannels;
    }

    public List<ConduitBlockConnection> insertConnections() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return Collections.unmodifiableList(insertConnections);
    }

    public List<ConduitBlockConnection> insertConnections(DyeColor color) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return insertConnectionsByChannel.get(color);
    }

    // This is sorted
    public List<ConduitBlockConnection> extractConnectionsFrom(ConduitBlockConnection insertConnection) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return extractConnectionsByInsert.getOrDefault(insertConnection, List.of());
    }

    public List<ConduitBlockConnection> extractConnections() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return Collections.unmodifiableList(extractConnections);
    }

    public List<ConduitBlockConnection> extractConnections(DyeColor color) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return extractConnectionsByChannel.get(color);
    }

    // This is sorted
    public List<ConduitBlockConnection> insertConnectionsFrom(ConduitBlockConnection extractConnection) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching as it has no ticker!");
        return insertConnectionsByExtract.getOrDefault(extractConnection, List.of());
    }

    // endregion

    // region Context

    public boolean hasContext(ConduitNetworkContextType<?> type) {
        ensureNotDiscarded();
        return context != null && context.type() == type;
    }

    @SuppressWarnings("unchecked")
    public <C extends ConduitNetworkContext<C>> @Nullable C getContext(ConduitNetworkContextType<C> type) {
        ensureNotDiscarded();
        if (context != null && context.type() == type) {
            return (C) context;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <C extends ConduitNetworkContext<C>> C getOrCreateContext(ConduitNetworkContextType<C> type) {
        ensureNotDiscarded();
        if (context != null && context.type() == type) {
            return (C) context;
        }

        context = type.factory().get();
        return (C) context;
    }

    // endregion

    // region Cache Updates

    /**
     * Call this before ticking the network to ensure caches are up-to-date.
     * We use this to defer cache rebuilds to the last possible moment to ensure network mutations are less expensive.
     */
    public void beforeTicking() {
        ensureNotDiscarded();

        // Shouldn't be called, but can't hurt to be safe.
        if (!supportsCaching) {
            return;
        }

        if (shouldRebuildCache) {
            rebuildCache();
        } else {
            for (var node : dirtyNodes) {
                if (node.isTicking()) {
                    // Update nodes by removing and adding again.
                    if (tickingNodes.contains(node)) {
                        removeTickingNode(node);
                    }

                    addTickingNode(node);
                } else if (tickingNodes.contains(node)) {
                    removeTickingNode(node);
                }
            }
        }

        if (haveConnectionsChanged) {
            updateChannelList();
            sortConnectionLists();
        }
    }

    public void onNodeUpdated(ConduitNode node) {
        if (supportsCaching && !shouldRebuildCache) {
            dirtyNodes.add(node);
        }
    }

    public void onChunkTickStatusChanged(long chunk) {
        if (!supportsCaching || shouldRebuildCache) {
            return;
        }

        dirtyNodes.addAll(nodesByChunkPos.get(chunk));
    }

    // endregion

    // region Caching Logic

    private void addTickingNode(ConduitNode node) {
        tickingNodes.add(node);

        for (var side : Direction.values()) {
            if (!node.isConnectedToBlock(side)) {
                continue;
            }

            var connection = new ConduitBlockConnection(node, side);
            endpointConnections.put(node, connection);

            // Add this connection to all other block connection's access maps
            for (var connectionList : accessibleBlockConnectionsMap.values()) {
                connectionList.add(connection);
            }

            // Add own list of block connection accesses.
            accessibleBlockConnectionsMap.computeIfAbsent(connection,
                    k -> new ArrayList<>(endpointConnections.values().size()));
            for (var accessibleConnection : endpointConnections.values()) {
                if (accessibleConnection != connection) {
                    accessibleBlockConnectionsMap.get(connection).add(accessibleConnection);
                }
            }

            // Handle IO caching
            var config = node.getConnectionConfig(side);
            if (config instanceof IOConnectionConfig ioConnectionConfig) {
                // First add sending and receiving connections
                boolean canInsert = ioConnectionConfig.canInsert(node::hasRedstoneSignal);
                boolean canExtract = ioConnectionConfig.canExtract(node::hasRedstoneSignal);

                if (canInsert) {
                    insertConnections.add(connection);
                    insertConnectionsByChannel.put(ioConnectionConfig.insertChannel(), connection);
                }

                if (canExtract) {
                    extractConnections.add(connection);
                    extractConnectionsByChannel.put(ioConnectionConfig.extractChannel(), connection);
                }

                // Now handle the mappings between them, do it after both are added in case we
                // can self-feed.
                if (canInsert) {
                    extractConnectionsByInsert.computeIfAbsent(connection, k -> new ArrayList<>())
                            .addAll(extractConnectionsByChannel.get(ioConnectionConfig.insertChannel()));

                    for (var receiver : extractConnectionsByChannel.get(ioConnectionConfig.insertChannel())) {
                        insertConnectionsByExtract.computeIfAbsent(receiver, k -> new ArrayList<>()).add(connection);
                    }
                }

                if (canExtract) {
                    insertConnectionsByExtract.computeIfAbsent(connection, k -> new ArrayList<>())
                            .addAll(insertConnectionsByChannel.get(ioConnectionConfig.extractChannel()));

                    for (var sender : insertConnectionsByChannel.get(ioConnectionConfig.extractChannel())) {
                        if (sender != connection) {
                            extractConnectionsByInsert.computeIfAbsent(sender, k -> new ArrayList<>()).add(connection);
                        }
                    }
                }
            }

            // All lists now require sorting
            haveConnectionsChanged = true;
        }
    }

    private void removeTickingNode(ConduitNode node) {
        if (!tickingNodes.contains(node)) {
            return;
        }

        tickingNodes.remove(node);

        // Remove connections from any maps
        for (var connection : endpointConnections.get(node)) {
            // Remove this connection's maps
            accessibleBlockConnectionsMap.remove(connection);

            // Not a fan of having to iterate, but it's probably fine.
            for (var color : DyeColor.values()) {
                insertConnections.remove(connection);
                extractConnections.remove(connection);
                insertConnectionsByChannel.remove(color, connection);
                extractConnectionsByChannel.remove(color, connection);
            }

            extractConnectionsByInsert.remove(connection);
            insertConnectionsByExtract.remove(connection);

            // Remove this connection from other maps
            for (var list : accessibleBlockConnectionsMap.values()) {
                list.remove(connection);
            }

            for (var list : extractConnectionsByInsert.values()) {
                list.remove(connection);
            }

            for (var list : insertConnectionsByExtract.values()) {
                list.remove(connection);
            }

            haveConnectionsChanged = true;
        }

        // And finally remove all the connections from the main list.
        endpointConnections.removeAll(node);
    }

    private void updateChannelList() {
        allChannels.clear();
        allChannels.addAll(insertConnectionsByChannel.keySet());
        allChannels.addAll(extractConnectionsByChannel.keySet());
    }

    private void sortConnectionLists() {
        var basicConnectionComparator = conduit().value().getGeneralConnectionComparator();
        if (basicConnectionComparator != null) {
            insertConnections.sort(basicConnectionComparator);
            extractConnections.sort(basicConnectionComparator);
        }

        for (var entry : accessibleBlockConnectionsMap.entrySet()) {
            sortConnections(entry.getKey(), entry.getValue());
        }

        for (var entry : extractConnectionsByInsert.entrySet()) {
            sortConnections(entry.getKey(), entry.getValue());
        }

        for (var entry : insertConnectionsByExtract.entrySet()) {
            sortConnections(entry.getKey(), entry.getValue());
        }

        haveConnectionsChanged = false;
    }

    /**
     * Called whenever a network is created, split or merged.
     * For other network mutations, partial cache modifications are supported.
     */
    private void rebuildCache() {
        // Clear partial update lists
        dirtyNodes.clear();

        // Clear all caches
        nodesByChunkPos.clear();
        tickingNodes.clear();
        endpointConnections.clear();
        accessibleBlockConnectionsMap.clear();
        insertConnections.clear();
        extractConnections.clear();
        insertConnectionsByChannel.clear();
        extractConnectionsByChannel.clear();
        insertConnectionsByExtract.clear();
        extractConnectionsByInsert.clear();

        // Add each ticking node into the caches.
        for (var node : nodes()) {
            // Put nodes into the position map.
            addNodeToPositionMaps(node, true);

            if (node.isTicking()) {
                addTickingNode(node);
            }
        }

        // Populate channel set
        updateChannelList();

        // Sort all lists
        sortConnectionLists();

        // Fire chunk coverage update
        if (onChunkCoverageChanged != null) {
            onChunkCoverageChanged.accept(this);
        }

        // Rebuild complete
        shouldRebuildCache = false;
        haveConnectionsChanged = false;
    }

    private void sortConnections(ConduitBlockConnection ref, List<ConduitBlockConnection> connections) {
        connections.sort((a, b) -> conduit.value().compareNodes(ref, a, b));
    }

    private void addNodeToPositionMaps(ConduitNode node, boolean isRebuild) {
        // Put nodes into the position map.
        long chunk = ChunkPos.asLong(node.pos());
        boolean isNewChunk = !nodesByChunkPos.containsKey(chunk);
        nodesByChunkPos.put(chunk, node);

        if (!isRebuild && isNewChunk && onChunkCoverageChanged != null) {
            onChunkCoverageChanged.accept(this);
        }
    }

    private void removeNodeFromPositionMaps(ConduitNode node) {
        // Put nodes into the position map.
        long chunk = ChunkPos.asLong(node.pos());
        nodesByChunkPos.remove(chunk, node);

        boolean isRemovedChunk = !nodesByChunkPos.containsKey(chunk);
        if (isRemovedChunk && onChunkCoverageChanged != null) {
            onChunkCoverageChanged.accept(this);
        }
    }

    // endregion

    // region Network Impl

    @Override
    protected ConduitNetwork createEmpty() {
        return new ConduitNetwork(conduit);
    }

    @Override
    protected void onNodeAdded(ConduitNode node) {
        // If called during super constructor
        // TODO: Review this behaviour...
        if (nodesByChunkPos == null) {
            return;
        }

        if (shouldRebuildCache) {
            return;
        }

        addNodeToPositionMaps(node, false);
        dirtyNodes.add(node);
    }

    @Override
    protected void onNodeRemoved(ConduitNode node) {
        if (shouldRebuildCache) {
            return;
        }

        removeNodeFromPositionMaps(node);
        if (!shouldRebuildCache) {
            dirtyNodes.add(node);
        }
    }

    @Override
    protected void onMerged(ConduitNetwork other) {
        if (context != null && other.context != null) {
            context = context.mergeWith(other.castContext());
        } else if (context == null && other.context != null) {
            context = other.context;
        }

        // The cache will need to be rebuilt
        shouldRebuildCache = true;
    }

    private <Z extends ConduitNetworkContext<Z>> Z castContext() {
        // noinspection unchecked
        return (Z) Objects.requireNonNull(context);
    }

    @Override
    protected void onGraphSplit(Set<ConduitNetwork> newNetworks) {
        shouldRebuildCache = true;
        if (context == null) {
            return;
        }

        // Collect all networks for splitting
        var allNetworks = Stream.concat(Stream.of(this), newNetworks.stream()).collect(Collectors.toSet());

        // Handle the new graphs first
        for (var newNetwork : newNetworks) {
            newNetwork.context = context.split(newNetwork, allNetworks);
            if (newNetwork.context == context) {
                throw new IllegalStateException("Splitting context for a network of '" + conduit.getRegisteredName()
                        + "' resulted in the same context for multiple networks.");
            }

            newNetwork.shouldRebuildCache = true;
        }

        var newContext = context.split(this, allNetworks);
        if (newContext == context) {
            throw new IllegalStateException("Splitting context for a network of '" + conduit.getRegisteredName()
                    + "' resulted in the same context for multiple networks.");
        }

        context = newContext;
    }

    // endregion

    public void addCrashInfo(CrashReportCategory category) {
        category.setDetail("ShouldRebuildCache", shouldRebuildCache);
        category.setDetail("HaveConnectionsChanged", haveConnectionsChanged);
        category.setDetail("NodeCount", nodeCount());
        category.setDetail("TickingNodeCount", tickingNodes.size());
        category.setDetail("NodesByChunkPos", nodesByChunkPos.size());
        category.setDetail("DirtNodes", dirtyNodes.size());
        category.setDetail("AllChannels", allChannels.size());
        category.setDetail("InsertConnections", insertConnections.size());
        category.setDetail("ExtractConnections", extractConnections.size());
    }
}
