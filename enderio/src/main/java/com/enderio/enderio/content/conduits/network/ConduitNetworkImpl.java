package com.enderio.enderio.content.conduits.network;

import com.enderio.core.common.graph.Network;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.IOConnectionConfig;
import com.enderio.enderio.api.conduits.connection.ConduitBlockConnection;
import com.enderio.enderio.api.conduits.connection.path.ConduitConnectionPath;
import com.enderio.enderio.api.conduits.connection.path.ConnectionPathProperty;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.enderio.enderio.api.conduits.network.ConduitNetworkContext;
import com.enderio.enderio.api.conduits.network.ConduitNetworkContextType;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import com.enderio.enderio.content.conduits.network.pathing.ConduitPathingStrategy;
import com.enderio.enderio.content.conduits.network.pathing.PathfindingContext;
import com.enderio.enderio.content.conduits.network.pathing.BreadthFirstPathingStrategy;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConduitNetworkImpl extends Network<ConduitNetworkImpl, ConduitNodeImpl> implements ConduitNetwork, PathfindingContext {


    public static final Codec<ConduitNetworkImpl> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(ConduitType.CODEC.fieldOf("conduit_type").forGetter(i -> i.conduitType),
                    ConduitNetworkContext.GENERIC_CODEC.optionalFieldOf("context")
                            .forGetter(i -> i.context == null || !i.context.type().isPersistent() ? Optional.empty()
                                    : Optional.of(i.context)))
            .and(graphCodec(instance, ConduitNodeImpl.CODEC))
            .apply(instance, ConduitNetworkImpl::new));

    private final ConduitType<?, ?> conduitType;
    
    private final ConduitPathingStrategy pathfinder = new BreadthFirstPathingStrategy();

    @Nullable
    private ConduitNetworkContext<?> context;
    
    // TODO: Not final because onNodeAdded is called by super so we need to initialize on use
    //       I might be able to make minor changes to the Network class to fix this.
    private final Map<Holder<Conduit<?, ?>>, Integer> nodeCountByConduit = Maps.newHashMap();

    // Caches
    private final boolean supportsCaching;

    private boolean shouldRebuildCache = true;
    private boolean haveConnectionsChanged = true;
    private final Set<ConduitNodeImpl> dirtyNodes = Sets.newHashSet();

    private final Multimap<Long, ConduitNodeImpl> nodesByChunkPos = HashMultimap.create();

    private final Set<ConduitNodeImpl> tickingNodes = Sets.newHashSet();

    // TODO: Separate this into a list and a multimap so we can sort all endpointConnections?
    private final SetMultimap<ConduitNodeImpl, ConduitBlockConnection> endpointConnections = HashMultimap.create();

    // Paths between connections are expensive to find, and most pairs are never queried.
    // So the path lists below are computed lazily (on first request) and cached.
    //
    // Cached lists carry the versions they were computed at. If the network has changed since, the list is stale
    // and is recomputed, but only within a small time budget per tick. Any stale list is served in the meantime
    // (minus any connections that no longer exist) so a change never causes a large lag spike.
    private final Map<ConduitBlockConnection, CachedPaths> accessiblePathsByConnection = Maps.newHashMap();

    private final List<ConduitBlockConnection> insertConnections = Lists.newArrayList();
    private final List<ConduitBlockConnection> extractConnections = Lists.newArrayList();

    private final Set<DyeColor> allChannels = Sets.newHashSet();
    private final ListMultimap<DyeColor, ConduitBlockConnection> insertConnectionsByChannel = ArrayListMultimap
            .create();
    private final ListMultimap<DyeColor, ConduitBlockConnection> extractConnectionsByChannel = ArrayListMultimap
            .create();

    private final Map<ConduitBlockConnection, CachedPaths> extractConnectionsByInsert = Maps.newHashMap();
    private final Map<ConduitBlockConnection, CachedPaths> insertConnectionsByExtract = Maps.newHashMap();

    private record CachedPaths(List<ConduitConnectionPath> paths, int topologyVersion, int membershipVersion) {
    }

    /**
     * The IO state of one connection, used to detect whether an update changed what paths could be found.
     */
    private record Membership(ConduitBlockConnection connection, @Nullable DyeColor insertChannel,
            @Nullable DyeColor extractChannel) {
    }

    // Versions, all taken from versionCounter so a version is never reused.
    private int versionCounter = 0;
    // Changes when the layout of the conduits changes (nodes removed, network rebuilt).
    private int topologyVersion = 0;
    // Changes when the set of endpoints changes.
    private int endpointVersion = 0;
    // Change when the connections that insert/extract on a channel change.
    private final Map<DyeColor, Integer> insertMembershipVersions = new EnumMap<>(DyeColor.class);
    private final Map<DyeColor, Integer> extractMembershipVersions = new EnumMap<>(DyeColor.class);

    // Time budget for searching for paths, so that recomputing paths is spread across many ticks.
    private static final long PATH_SEARCH_BUDGET_NANOS = 2_000_000L;
    private static final long PATH_SEARCH_WINDOW_NANOS = 50_000_000L;
    private long pathSearchWindowStart = 0;
    private long pathSearchNanosUsed = 0;

    // The channel each IO connection inserts to / extracts from, used to find the targets for lazy path lookups.
    private final Map<ConduitBlockConnection, DyeColor> insertChannelByConnection = Maps.newHashMap();
    private final Map<ConduitBlockConnection, DyeColor> extractChannelByConnection = Maps.newHashMap();

    private final ListMultimap<Integer, Holder<Conduit<?, ?>>> tickableConduits = ArrayListMultimap.create();
    private boolean areTickableConduitsValid = false;

    @Nullable
    private Consumer<ConduitNetworkImpl> onChunkCoverageChanged = null;

    public ConduitNetworkImpl(ConduitType<?, ?> conduitType, ConduitNodeImpl initialNode) {
        super(initialNode);
        this.conduitType = conduitType;
        this.supportsCaching = conduitType.doesRequireNetworkCaches();
        recomputeNodeCounts();
    }

    // TODO: Public for legacy deserialization
    public ConduitNetworkImpl(ConduitType<?, ?> conduitType, Optional<ConduitNetworkContext<?>> context,
                              List<ConduitNodeImpl> nodes, IndexedEdgeList edges) {
        super(nodes, edges);
        this.conduitType = conduitType;
        this.context = context.orElse(null);
        this.supportsCaching = conduitType.doesRequireNetworkCaches();
        recomputeNodeCounts();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ConduitNetworkImpl(Holder<Conduit<?, ?>> conduit, Optional<ConduitNetworkContext<?>> context,
                               List<ConduitNodeImpl> nodes, IndexedEdgeList edges) {
        super(nodes, edges);
        this.conduitType = conduit.value().type();
        this.context = context.orElse(null);
        this.supportsCaching = conduitType.doesRequireNetworkCaches();
        recomputeNodeCounts();
    }

    protected ConduitNetworkImpl(ConduitType<?, ?> conduitType) {
        this.conduitType = conduitType;
        this.supportsCaching = conduitType.doesRequireNetworkCaches();
    }

    @Override
    public ConduitType<?, ?> conduitType() {
        return conduitType;
    }

    @Override
    public Set<Holder<Conduit<?, ?>>> conduits() {
        if (nodeCountByConduit == null) {
            return Set.of();
        }

        return Collections.unmodifiableSet(nodeCountByConduit.keySet());
    }

    @Override
    public List<Holder<Conduit<?, ?>>> getTickableConduits(long gameTime, int tickOffset) {
        if (!areTickableConduitsValid && nodeCountByConduit != null) {
            tickableConduits.clear();

            // Check all conduits to see if they can tick
            for (var conduit : nodeCountByConduit.keySet()) {
                for (int i = 0; i < 20; i++) {
                    if (i % conduitType().getTickRate(conduit) == 0) {
                        tickableConduits.put(i, conduit);
                    }
                }
            }

            areTickableConduitsValid = true;
        }

        return tickableConduits.get(Ints.saturatedCast((gameTime + tickOffset) % 20));
    }

    // region Chunk Tracking

    public Set<Long> allChunks() {
        return nodesByChunkPos.keySet();
    }

    public void setOnChunkCoverageChanged(Consumer<ConduitNetworkImpl> onChunkCoverageChanged) {
        this.onChunkCoverageChanged = onChunkCoverageChanged;
    }

    // endregion

    // region Queries

    @Override
    public int nodeCount(Holder<Conduit<?, ?>> conduit) {
        return nodeCountByConduit.getOrDefault(conduit, 0);
    }

    private void recomputeNodeCounts() {
        nodeCountByConduit.clear();
        for (var node : nodes()) {
            nodeCountByConduit.compute(node.conduit(), (k, count) -> count == null ? 1 : count + 1);
        }
    }

    // These are unfortunately necessary for the IConduitNetwork interface.
    @Override
    public boolean contains(ConduitNode node) {
        if (node instanceof ConduitNodeImpl typedNode) {
            return contains(typedNode);
        }

        return false;
    }

    @Override
    public Set<? extends ConduitNode> neighbors(ConduitNode node) {
        if (node instanceof ConduitNodeImpl typedNode) {
            return neighbors(typedNode);
        }

        return Set.of();
    }

    public Collection<ConduitNodeImpl> tickingNodes() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");
        return Collections.unmodifiableCollection(tickingNodes);
    }

    public Collection<ConduitNodeImpl> blockEndpoints() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");
        return Collections.unmodifiableCollection(endpointConnections.keySet());
    }

    public Collection<ConduitBlockConnection> blockConnections() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");
        return Collections.unmodifiableCollection(endpointConnections.values());
    }

    // This is sorted
    public List<ConduitConnectionPath> blockConnectionsAccessibleFrom(ConduitBlockConnection connection) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");

        if (!endpointConnections.containsEntry(connection.node(), connection)) {
            return List.of();
        }

        return getCachedPaths(accessiblePathsByConnection, connection, endpointVersion, () -> {
            var targets = new ArrayList<ConduitBlockConnection>(endpointConnections.size());
            for (var other : endpointConnections.values()) {
                if (!other.equals(connection)) {
                    targets.add(other);
                }
            }
            return targets;
        }, end -> endpointConnections.containsEntry(end.node(), end));
    }

    /**
     * Get the cached paths for a connection, (re)computing them if needed and if there is time budget to do so.
     * <p>
     * If the cached paths are out of date and the budget is spent, the out of date paths are returned (without any
     * paths to connections that no longer exist) and are refreshed in a later tick. If there are no cached paths at
     * all, an empty list is returned until they could be computed.
     * </p>
     */
    private List<ConduitConnectionPath> getCachedPaths(Map<ConduitBlockConnection, CachedPaths> cache,
            ConduitBlockConnection key, int membershipVersion,
            Supplier<? extends Collection<ConduitBlockConnection>> targets,
            Predicate<ConduitBlockConnection> isValidEnd) {
        var cached = cache.get(key);
        if (cached != null && cached.topologyVersion() == topologyVersion
                && cached.membershipVersion() == membershipVersion) {
            return cached.paths();
        }

        if (hasPathSearchBudget()) {
            long searchStart = System.nanoTime();
            var paths = computeSortedPaths(key, targets.get());
            pathSearchNanosUsed += System.nanoTime() - searchStart;

            cache.put(key, new CachedPaths(paths, topologyVersion, membershipVersion));
            return paths;
        }

        if (cached == null) {
            // Nothing to serve yet, this will be computed in a later tick.
            return List.of();
        }

        // Serve the out of date paths for now, but never ones that lead to connections that have gone.
        var stalePaths = cached.paths();
        boolean allValid = true;
        for (var path : stalePaths) {
            if (!isValidEnd.test(path.end())) {
                allValid = false;
                break;
            }
        }

        if (allValid) {
            return stalePaths;
        }

        var validPaths = new ArrayList<ConduitConnectionPath>(stalePaths.size());
        for (var path : stalePaths) {
            if (isValidEnd.test(path.end())) {
                validPaths.add(path);
            }
        }

        cache.put(key, new CachedPaths(validPaths, cached.topologyVersion(), cached.membershipVersion()));
        return validPaths;
    }

    private boolean hasPathSearchBudget() {
        long now = System.nanoTime();
        if (now - pathSearchWindowStart >= PATH_SEARCH_WINDOW_NANOS) {
            pathSearchWindowStart = now;
            pathSearchNanosUsed = 0;
        }

        return pathSearchNanosUsed < PATH_SEARCH_BUDGET_NANOS;
    }

    /**
     * Find the paths from a connection to the targets, sorted by the conduit type's comparator.
     */
    private List<ConduitConnectionPath> computeSortedPaths(ConduitBlockConnection from,
            Collection<ConduitBlockConnection> targets) {
        var paths = new ArrayList<>(findBestPathsTo(from, targets).values());
        paths.sort(conduitType.connectionPathComparator());
        return paths;
    }

    private Set<Membership> membershipOf(ConduitNodeImpl node) {
        var memberships = new HashSet<Membership>();
        for (var connection : endpointConnections.get(node)) {
            memberships.add(new Membership(connection, insertChannelByConnection.get(connection),
                    extractChannelByConnection.get(connection)));
        }
        return memberships;
    }

    /**
     * Called when a node's endpoints or IO configuration changed, marks the path lists that this affects as stale.
     */
    private void onMembershipChanged(Set<Membership> before, Set<Membership> after) {
        endpointVersion = ++versionCounter;

        for (var memberships : List.of(before, after)) {
            for (var membership : memberships) {
                if (membership.insertChannel() != null) {
                    insertMembershipVersions.put(membership.insertChannel(), ++versionCounter);
                }

                if (membership.extractChannel() != null) {
                    extractMembershipVersions.put(membership.extractChannel(), ++versionCounter);
                }
            }
        }

        // Forget cached paths for connections that no longer exist.
        var remaining = new HashSet<ConduitBlockConnection>();
        for (var membership : after) {
            remaining.add(membership.connection());
        }

        for (var membership : before) {
            var connection = membership.connection();
            if (!remaining.contains(connection)) {
                accessiblePathsByConnection.remove(connection);
                extractConnectionsByInsert.remove(connection);
                insertConnectionsByExtract.remove(connection);
            }
        }
    }

    public Set<DyeColor> allChannels() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");
        return allChannels;
    }

    public List<ConduitBlockConnection> insertConnections() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");
        return Collections.unmodifiableList(insertConnections);
    }

    public List<ConduitBlockConnection> insertConnections(DyeColor color) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");
        return insertConnectionsByChannel.get(color);
    }

    // This is sorted
    public List<ConduitConnectionPath> extractConnectionsFrom(ConduitBlockConnection insertConnection) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");

        var channel = insertChannelByConnection.get(insertConnection);
        if (channel == null) {
            return List.of();
        }

        return getCachedPaths(extractConnectionsByInsert, insertConnection,
                extractMembershipVersions.getOrDefault(channel, 0),
                () -> extractConnectionsByChannel.get(channel),
                end -> extractChannelByConnection.get(end) == channel);
    }

    public List<ConduitBlockConnection> extractConnections() {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");
        return Collections.unmodifiableList(extractConnections);
    }

    public List<ConduitBlockConnection> extractConnections(DyeColor color) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");
        return extractConnectionsByChannel.get(color);
    }

    // This is sorted
    public List<ConduitConnectionPath> insertConnectionsFrom(ConduitBlockConnection extractConnection) {
        ensureNotDiscarded();
        Preconditions.checkState(supportsCaching, "This conduit does not support caching");

        var channel = extractChannelByConnection.get(extractConnection);
        if (channel == null) {
            return List.of();
        }

        return getCachedPaths(insertConnectionsByExtract, extractConnection,
                insertMembershipVersions.getOrDefault(channel, 0),
                () -> insertConnectionsByChannel.get(channel),
                end -> insertChannelByConnection.get(end) == channel);
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
    public void ensureCachesReady() {
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
                        var before = membershipOf(node);
                        removeTickingNode(node);
                        addTickingNode(node);

                        // Only mark paths as stale if the update changed which connections can send/receive.
                        // Most updates (e.g. chunk load/unload) don't, and cached paths remain valid.
                        var after = membershipOf(node);
                        if (!before.equals(after)) {
                            onMembershipChanged(before, after);
                        }
                    } else {
                        addTickingNode(node);
                        onMembershipChanged(Set.of(), membershipOf(node));
                    }
                } else if (tickingNodes.contains(node)) {
                    var before = membershipOf(node);
                    removeTickingNode(node);
                    onMembershipChanged(before, Set.of());
                }
            }

            dirtyNodes.clear();
        }

        if (haveConnectionsChanged) {
            updateChannelList();
            sortConnectionLists();
        }
    }

    public void onNodeUpdated(ConduitNodeImpl node) {
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

    private void addTickingNode(ConduitNodeImpl node) {
        tickingNodes.add(node);

        for (var side : Direction.values()) {
            if (!node.isConnectedToBlock(side)) {
                continue;
            }

            var connection = new ConduitBlockConnection(node, side);
            endpointConnections.put(node, connection);

            // Handle IO caching
            var config = node.getConnectionConfig(side);
            if (config instanceof IOConnectionConfig ioConnectionConfig) {
                // First add sending and receiving connections
                boolean canInsert = ioConnectionConfig.canInsert(node::hasRedstoneSignal);
                boolean canExtract = ioConnectionConfig.canExtract(node::hasRedstoneSignal);

                // The paths between these connections are found lazily, see extractConnectionsFrom/insertConnectionsFrom.
                if (canInsert) {
                    insertConnections.add(connection);
                    insertConnectionsByChannel.put(ioConnectionConfig.insertChannel(), connection);
                    insertChannelByConnection.put(connection, ioConnectionConfig.insertChannel());
                }

                if (canExtract) {
                    extractConnections.add(connection);
                    extractConnectionsByChannel.put(ioConnectionConfig.extractChannel(), connection);
                    extractChannelByConnection.put(connection, ioConnectionConfig.extractChannel());
                }
            }

            // All lists now require sorting
            haveConnectionsChanged = true;
        }
    }

    private Map<ConduitBlockConnection, ConduitConnectionPath> findBestPathsTo(ConduitBlockConnection from,
            Collection<ConduitBlockConnection> targets) {
        return pathfinder.findPaths(from, targets, this);
    }
    
    // region PathfindingContext Implementation
    
    @Override
    public Map<ConnectionPathProperty<?>, Object> collectNodeProperties(ConduitNode node) {
        Map<ConnectionPathProperty<?>, Object> values = Maps.newHashMap();
        node.conduit().value().collectNodePathProperties(node, values::put);
        return values;
    }

    // endregion

    private void removeTickingNode(ConduitNodeImpl node) {
        if (!tickingNodes.contains(node)) {
            return;
        }

        tickingNodes.remove(node);

        // Remove connections from any maps
        for (var connection : endpointConnections.get(node)) {
            // Not a fan of having to iterate, but it's probably fine.
            for (var color : DyeColor.values()) {
                insertConnections.remove(connection);
                extractConnections.remove(connection);
                insertConnectionsByChannel.remove(color, connection);
                extractConnectionsByChannel.remove(color, connection);
            }

            insertChannelByConnection.remove(connection);
            extractChannelByConnection.remove(connection);

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
        var basicConnectionComparator = conduitType.connectionComparator();
        if (basicConnectionComparator != null) {
            insertConnections.sort(basicConnectionComparator);
            extractConnections.sort(basicConnectionComparator);
        }

        // Path lists are sorted as they are lazily computed.

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
        areTickableConduitsValid = false;

        nodesByChunkPos.clear();
        tickingNodes.clear();
        endpointConnections.clear();
        insertConnections.clear();
        extractConnections.clear();
        insertConnectionsByChannel.clear();
        extractConnectionsByChannel.clear();
        insertChannelByConnection.clear();
        extractChannelByConnection.clear();

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

        // The layout has changed, so all cached paths are stale. Rather than throwing them away, they are
        // kept and served until they are recomputed, which is spread across ticks. Paths to connections that
        // are no longer in this network are filtered out when served.
        topologyVersion = ++versionCounter;
        endpointVersion = ++versionCounter;
        for (var color : DyeColor.values()) {
            insertMembershipVersions.put(color, ++versionCounter);
            extractMembershipVersions.put(color, ++versionCounter);
        }

        // Forget cached paths for connections no longer in this network.
        accessiblePathsByConnection.keySet().removeIf(c -> !endpointConnections.containsEntry(c.node(), c));
        extractConnectionsByInsert.keySet().removeIf(c -> !insertChannelByConnection.containsKey(c));
        insertConnectionsByExtract.keySet().removeIf(c -> !extractChannelByConnection.containsKey(c));

        // Fire chunk coverage update
        if (onChunkCoverageChanged != null) {
            onChunkCoverageChanged.accept(this);
        }

        // Rebuild complete
        shouldRebuildCache = false;
        haveConnectionsChanged = false;
    }

    private void addNodeToPositionMaps(ConduitNodeImpl node, boolean isRebuild) {
        // Put nodes into the position map.
        long chunk = ChunkPos.pack(node.pos());
        boolean isNewChunk = !nodesByChunkPos.containsKey(chunk);
        nodesByChunkPos.put(chunk, node);

        if (!isRebuild && isNewChunk && onChunkCoverageChanged != null) {
            onChunkCoverageChanged.accept(this);
        }
    }

    private void removeNodeFromPositionMaps(ConduitNodeImpl node) {
        // Put nodes into the position map.
        long chunk = ChunkPos.pack(node.pos());
        nodesByChunkPos.remove(chunk, node);

        boolean isRemovedChunk = !nodesByChunkPos.containsKey(chunk);
        if (isRemovedChunk && onChunkCoverageChanged != null) {
            onChunkCoverageChanged.accept(this);
        }
    }

    // endregion

    // region Network Impl

    @Override
    protected ConduitNetworkImpl createEmpty() {
        return new ConduitNetworkImpl(conduitType);
    }

    @Override
    protected void onNodeAdded(ConduitNodeImpl node) {
        // Always recompute what conduits can tick - it's cheap to do.
        areTickableConduitsValid = false;

        // Increment node count if loaded (constructor will initialize the values)
        if (nodeCountByConduit != null) {
            nodeCountByConduit.compute(node.conduit(), (k, count) -> count == null ? 1 : count + 1);
        }

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
    protected void onNodeRemoved(ConduitNodeImpl node) {
        // Always recompute what conduits can tick - it's cheap to do.
        areTickableConduitsValid = false;

        // Paths may have gone through this node, so they are now stale.
        topologyVersion = ++versionCounter;

        // Remove from node count
        nodeCountByConduit.compute(node.conduit(), (k, count) -> count == null || count == 1 ? null : count - 1);

        if (shouldRebuildCache) {
            return;
        }

        removeNodeFromPositionMaps(node);
        if (!shouldRebuildCache) {
            dirtyNodes.add(node);
        }
    }

    @Override
    protected void onMerged(ConduitNetworkImpl other) {
        if (context != null && other.context != null) {
            context = context.mergeWith(other.castContext());
        } else if (context == null && other.context != null) {
            context = other.context;
        }

        // Fully recompute node counts
        recomputeNodeCounts();

        // The cache will need to be rebuilt
        shouldRebuildCache = true;
    }

    private <Z extends ConduitNetworkContext<Z>> Z castContext() {
        // noinspection unchecked
        return (Z) Objects.requireNonNull(context);
    }

    @Override
    protected void onGraphSplit(Set<ConduitNetworkImpl> newNetworks) {
        shouldRebuildCache = true;

        // Fully recompute node counts
        recomputeNodeCounts();

        if (context == null) {
            return;
        }

        // Collect all networks for splitting
        var allNetworks = Stream.concat(Stream.of(this), newNetworks.stream()).collect(Collectors.toSet());

        // Handle the new graphs first
        for (var newNetwork : newNetworks) {
            newNetwork.context = context.split(newNetwork, allNetworks);
            if (newNetwork.context == context) {
                throw new IllegalStateException("Splitting context for a network of '" + EnderIORegistries.CONDUIT_TYPE.getKey(conduitType)
                        + "' resulted in the same context for multiple networks.");
            }

            newNetwork.shouldRebuildCache = true;
        }

        var newContext = context.split(this, allNetworks);
        if (newContext == context) {
            throw new IllegalStateException("Splitting context for a network of '" + EnderIORegistries.CONDUIT_TYPE.getKey(conduitType)
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
        category.setDetail("DirtyNodes", dirtyNodes.size());
        category.setDetail("AllChannels", allChannels.size());
        category.setDetail("InsertConnections", insertConnections.size());
        category.setDetail("ExtractConnections", extractConnections.size());
    }
}
