package com.enderio.conduits.common.conduit.new_graph;

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
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

public class ConduitNetwork extends Network<ConduitNetwork, ConduitNode> implements IConduitNetwork {

    public static final Codec<ConduitNetwork> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(Conduit.CODEC.fieldOf("conduit").forGetter(i -> i.conduit),
                    ConduitNetworkContext.GENERIC_CODEC.optionalFieldOf("context")
                            .forGetter(i -> Optional.ofNullable(i.context)))
            .and(graphCodec(instance, ConduitNode.CODEC))
            .apply(instance, ConduitNetwork::new));

    private final Holder<Conduit<?, ?>> conduit;

    @Nullable
    private ConduitNetworkContext<?> context;

    // Caches
    private boolean shouldRebuildCache = true;
    private boolean haveConnectionsChanged = true;

    private final Multimap<Long, ConduitNode> nodesByChunkPos = HashMultimap.create();

    private final Set<ConduitNode> loadedNodes = Sets.newHashSet();

    private final SetMultimap<ConduitNode, ConduitBlockConnection> endpointConnections = HashMultimap.create();
    private final Map<ConduitBlockConnection, List<ConduitBlockConnection>> accessibleBlockConnectionsMap = Maps
            .newHashMap();

    private final List<ConduitBlockConnection> sendingConnections = Lists.newArrayList();
    private final List<ConduitBlockConnection> receivingConnections = Lists.newArrayList();

    private final Set<DyeColor> allChannels = Sets.newHashSet();
    private final ListMultimap<DyeColor, ConduitBlockConnection> sendingConnectionsByChannel = ArrayListMultimap
            .create();
    private final ListMultimap<DyeColor, ConduitBlockConnection> receivingConnectionsByChannel = ArrayListMultimap
            .create();

    private final Map<ConduitBlockConnection, List<ConduitBlockConnection>> receivingConnectionsBySender = Maps
            .newHashMap();
    private final Map<ConduitBlockConnection, List<ConduitBlockConnection>> sendingConnectionsByReceiver = Maps
            .newHashMap();

    private Consumer<ConduitNetwork> onChunkCoverageChanged;

    public ConduitNetwork(Holder<Conduit<?, ?>> conduit, ConduitNode initialNode) {
        super(initialNode);
        this.conduit = conduit;
    }

    // TODO: Only public for legacy deserialisation.
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public ConduitNetwork(Holder<Conduit<?, ?>> conduit, Optional<ConduitNetworkContext<?>> context,
            List<ConduitNode> nodes, IndexedEdgeList edges) {
        super(nodes, edges);
        this.conduit = conduit;
        this.context = context.orElse(null);
    }

    protected ConduitNetwork(Holder<Conduit<?, ?>> conduit) {
        this.conduit = conduit;
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

    public Collection<ConduitNode> loadedNodes() {
        ensureNotDiscarded();
        return Collections.unmodifiableCollection(loadedNodes);
    }

    public Collection<ConduitNode> blockEndpoints() {
        ensureNotDiscarded();
        return Collections.unmodifiableCollection(endpointConnections.keySet());
    }

    public Collection<ConduitBlockConnection> blockConnections() {
        ensureNotDiscarded();
        return Collections.unmodifiableCollection(endpointConnections.values());
    }

    // This is sorted
    public List<ConduitBlockConnection> blockConnectionsAccessibleFrom(ConduitBlockConnection connection) {
        ensureNotDiscarded();
        return accessibleBlockConnectionsMap.getOrDefault(connection, List.of());
    }

    public Set<DyeColor> allChannels() {
        ensureNotDiscarded();
        return allChannels;
    }

    public List<ConduitBlockConnection> sendingConnections() {
        ensureNotDiscarded();
        return Collections.unmodifiableList(sendingConnections);
    }

    public List<ConduitBlockConnection> sendingConnections(DyeColor color) {
        ensureNotDiscarded();
        return sendingConnectionsByChannel.get(color);
    }

    // This is sorted
    public List<ConduitBlockConnection> receivingConnectionsFrom(ConduitBlockConnection sender) {
        ensureNotDiscarded();
        return receivingConnectionsBySender.getOrDefault(sender, List.of());
    }

    public List<ConduitBlockConnection> receivingConnections() {
        ensureNotDiscarded();
        return Collections.unmodifiableList(receivingConnections);
    }

    public List<ConduitBlockConnection> receivingConnections(DyeColor color) {
        ensureNotDiscarded();
        return receivingConnectionsByChannel.get(color);
    }

    // This is sorted
    public List<ConduitBlockConnection> sendingConnectionsFrom(ConduitBlockConnection receiverNode) {
        ensureNotDiscarded();
        return sendingConnectionsByReceiver.getOrDefault(receiverNode, List.of());
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

        if (shouldRebuildCache) {
            rebuildCache();
        }

        if (haveConnectionsChanged) {
            updateChannelList();
            sortConnectionLists();
        }
    }

    public void onNodeLoaded(ConduitNode node) {
        Preconditions.checkArgument(node.isLoaded(), "Node is not loaded!");

        // If a full rebuild is scheduled, don't modify the cache
        if (shouldRebuildCache) {
            return;
        }

        addLoadedNode(node);
    }

    public void onNodeUnloaded(ConduitNode node) {
        Preconditions.checkArgument(!node.isLoaded(), "Node is still loaded!");

        // If a full rebuild is scheduled, don't modify the cache
        if (shouldRebuildCache) {
            return;
        }

        removeLoadedNode(node);
    }

    public void onNodeUpdated(ConduitNode node) {
        Preconditions.checkArgument(node.isLoaded(), "Node is not loaded!");

        if (shouldRebuildCache) {
            return;
        }

        // If we've somehow missed this node, do the full works
        if (!loadedNodes.contains(node)) {
            addLoadedNode(node);
        } else {
            // TODO: We should do this with a partial edit rather than remove then add
            // again...
            // This will do for testing though
            removeLoadedNode(node);
            addLoadedNode(node);
        }

        // Update channel list
        updateChannelList();

        // Must re-sort in case ordering has changed.
        haveConnectionsChanged = true;
    }

    public void onChunkTickStatusChanged(long chunk) {
        for (var node : nodesByChunkPos.get(chunk)) {
            if (node.isLoaded()) {
                addLoadedNode(node);
            } else {
                removeLoadedNode(node);
            }
        }
    }

    // endregion

    // region Caching Logic

    private void addLoadedNode(ConduitNode node) {
        loadedNodes.add(node);

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
                boolean canSend = ioConnectionConfig.canSend(node::hasRedstoneSignal);
                boolean canReceive = ioConnectionConfig.canReceive(node::hasRedstoneSignal);

                if (canSend) {
                    sendingConnections.add(connection);
                    sendingConnectionsByChannel.put(ioConnectionConfig.sendColor(), connection);
                }

                if (canReceive) {
                    receivingConnections.add(connection);
                    receivingConnectionsByChannel.put(ioConnectionConfig.receiveColor(), connection);
                }

                // Now handle the mappings between them, do it after both are added in case we
                // can self-feed.
                if (canSend) {
                    receivingConnectionsBySender.computeIfAbsent(connection, k -> new ArrayList<>())
                            .addAll(receivingConnectionsByChannel.get(ioConnectionConfig.sendColor()));

                    for (var receiver : receivingConnectionsByChannel.get(ioConnectionConfig.sendColor())) {
                        sendingConnectionsByReceiver.computeIfAbsent(receiver, k -> new ArrayList<>()).add(connection);
                    }
                }

                if (canReceive) {
                    sendingConnectionsByReceiver.computeIfAbsent(connection, k -> new ArrayList<>())
                            .addAll(sendingConnectionsByChannel.get(ioConnectionConfig.receiveColor()));

                    for (var sender : sendingConnectionsByChannel.get(ioConnectionConfig.receiveColor())) {
                        if (sender != connection) {
                            receivingConnectionsBySender.computeIfAbsent(sender, k -> new ArrayList<>())
                                    .add(connection);
                        }
                    }
                }
            }

            // All lists now require sorting
            haveConnectionsChanged = true;
        }
    }

    private void removeLoadedNode(ConduitNode node) {
        if (!loadedNodes.contains(node)) {
            return;
        }

        loadedNodes.remove(node);

        // Remove connections from any maps
        for (var connection : endpointConnections.get(node)) {
            // Remove this connection's maps
            accessibleBlockConnectionsMap.remove(connection);

            // Not a fan of having to iterate, but it's probably fine.
            for (var color : DyeColor.values()) {
                sendingConnections.remove(connection);
                receivingConnections.remove(connection);
                sendingConnectionsByChannel.remove(color, connection);
                receivingConnectionsByChannel.remove(color, connection);
            }

            receivingConnectionsBySender.remove(connection);
            sendingConnectionsByReceiver.remove(connection);

            // Remove this connection from other maps
            for (var list : accessibleBlockConnectionsMap.values()) {
                list.remove(connection);
            }

            for (var list : receivingConnectionsBySender.values()) {
                list.remove(connection);
            }

            for (var list : sendingConnectionsByReceiver.values()) {
                list.remove(connection);
            }

            haveConnectionsChanged = true;
        }

        // And finally remove all the connections from the main list.
        endpointConnections.removeAll(node);
    }

    private void updateChannelList() {
        allChannels.clear();
        allChannels.addAll(sendingConnectionsByChannel.keySet());
        allChannels.addAll(receivingConnectionsByChannel.keySet());
    }

    private void sortConnectionLists() {
        for (var entry : accessibleBlockConnectionsMap.entrySet()) {
            sortConnections(entry.getKey(), entry.getValue());
        }

        for (var entry : receivingConnectionsBySender.entrySet()) {
            sortConnections(entry.getKey(), entry.getValue());
        }

        for (var entry : sendingConnectionsByReceiver.entrySet()) {
            sortConnections(entry.getKey(), entry.getValue());
        }

        haveConnectionsChanged = false;
    }

    /**
     * Called whenever a network is created, split or merged.
     * For other network mutations, partial cache modifications are supported.
     */
    private void rebuildCache() {
        nodesByChunkPos.clear();
        loadedNodes.clear();
        endpointConnections.clear();
        accessibleBlockConnectionsMap.clear();
        sendingConnections.clear();
        receivingConnections.clear();
        sendingConnectionsByChannel.clear();
        receivingConnectionsByChannel.clear();
        sendingConnectionsByReceiver.clear();
        receivingConnectionsBySender.clear();

        // Add each loaded node into the caches.
        for (var node : nodes()) {
            // Put nodes into the position map.
            addNodeToPositionMaps(node, true);

            if (node.isLoaded()) {
                addLoadedNode(node);
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
        connections.sort((a, b) -> conduit.value().compare(ref, a, b));
    }

    private void addNodeToPositionMaps(ConduitNode node, boolean isRebuild) {
        // Put nodes into the position map.
        long chunk = ChunkPos.asLong(node.pos());
        boolean isNewChunk = !nodesByChunkPos.containsKey(chunk);
        nodesByChunkPos.put(chunk, node);

        if (!isRebuild && isNewChunk) {
            onChunkCoverageChanged.accept(this);
        }
    }

    private void removeNodeFromPositionMaps(ConduitNode node) {
        // Put nodes into the position map.
        long chunk = ChunkPos.asLong(node.pos());
        nodesByChunkPos.remove(chunk, node);

        boolean isRemovedChunk = !nodesByChunkPos.containsKey(chunk);
        if (isRemovedChunk) {
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
        if (node.isLoaded()) {
            addLoadedNode(node);
        }
    }

    @Override
    protected void onNodeRemoved(ConduitNode node) {
        if (shouldRebuildCache) {
            return;
        }

        removeNodeFromPositionMaps(node);
        removeLoadedNode(node);
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

        // TODO: Implement proper split method for contexts!

        // Handle the new graphs first
        for (var newNetwork : newNetworks) {
            newNetwork.context = context.copy();
            newNetwork.shouldRebuildCache = true;
        }

        context = context.copy();
    }

    // endregion
}
