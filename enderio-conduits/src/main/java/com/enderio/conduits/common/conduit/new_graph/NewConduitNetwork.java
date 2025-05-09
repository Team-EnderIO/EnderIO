package com.enderio.conduits.common.conduit.new_graph;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.connection.config.IOConnectionConfig;
import com.enderio.conduits.api.network.ConduitNetworkContext;
import com.enderio.conduits.api.network.ConduitNetworkContextType;
import com.enderio.core.common.graph.Network;
import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class NewConduitNetwork extends Network<NewConduitNetwork, NewConduitNode> {

    // TODO: Need to test this, we won't use this Codec unless I can be bothered to convert saves or when we update to 1.22
    public static final Codec<NewConduitNetwork> CODEC = RecordCodecBuilder.create(instance -> instance
        .group(Conduit.CODEC.fieldOf("conduit").forGetter(i -> i.conduit),
            ConduitNetworkContext.GENERIC_CODEC.optionalFieldOf("context").forGetter(i -> Optional.ofNullable(i.context)))
        .and(graphCodec(instance, NewConduitNode.NEW_CODEC))
        .apply(instance, NewConduitNetwork::new));

    private final Holder<Conduit<?, ?>> conduit;

    @Nullable
    private ConduitNetworkContext<?> context;

    // Caches
    private boolean shouldRebuildCache = true;
    private boolean haveConnectionsChanged = true;
    private final Set<NewConduitNode> loadedNodes = Sets.newHashSet();

    private final SetMultimap<NewConduitNode, BlockConnection> endpointConnections = HashMultimap.create();
    private final Map<BlockConnection, List<BlockConnection>> accessibleBlockConnectionsMap = Maps.newHashMap();

    private final Set<DyeColor> allChannels = Sets.newHashSet();
    private final SetMultimap<DyeColor, BlockConnection> sendingConnections = HashMultimap.create();
    private final SetMultimap<DyeColor, BlockConnection> receivingConnections = HashMultimap.create();

    private final Map<BlockConnection, List<BlockConnection>> receivingConnectionsBySender = Maps.newHashMap();
    private final Map<BlockConnection, List<BlockConnection>> sendingConnectionsByReceiver = Maps.newHashMap();

    public NewConduitNetwork(Holder<Conduit<?, ?>> conduit, NewConduitNode initialNode) {
        super(initialNode);
        this.conduit = conduit;
    }

    private NewConduitNetwork(Holder<Conduit<?, ?>> conduit, Optional<ConduitNetworkContext<?>> context, List<NewConduitNode> nodes, IndexedEdgeList edges) {
        super(nodes, edges);
        this.conduit = conduit;
        this.context = context.orElse(null);
    }

    protected NewConduitNetwork(Holder<Conduit<?, ?>> conduit) {
        this.conduit = conduit;
    }

    // region Queries

    public Collection<NewConduitNode> loadedNodes() {
        return Collections.unmodifiableCollection(loadedNodes);
    }

    public Collection<NewConduitNode> blockEndpoints() {
        return Collections.unmodifiableCollection(endpointConnections.keySet());
    }

    public Collection<BlockConnection> blockConnections() {
        return Collections.unmodifiableCollection(endpointConnections.values());
    }

    // This is sorted
    public List<BlockConnection> blockConnectionsAccessibleFrom(BlockConnection connection) {
        return accessibleBlockConnectionsMap.getOrDefault(connection, List.of());
    }

    public Set<DyeColor> allChannels() {
        return allChannels;
    }

    public Collection<BlockConnection> sendingConnections() {
        return sendingConnections.values();
    }

    public Collection<BlockConnection> sendingConnections(DyeColor color) {
        return sendingConnections.get(color);
    }

    // This is sorted
    public List<BlockConnection> receivingConnectionsFrom(BlockConnection sender) {
        return receivingConnectionsBySender.getOrDefault(sender, List.of());
    }

    public Collection<BlockConnection> receivingConnections() {
        return receivingConnections.values();
    }

    public Collection<BlockConnection> receivingConnections(DyeColor color) {
        return receivingConnections.get(color);
    }

    // This is sorted
    public List<BlockConnection> sendingConnectionsFrom(BlockConnection receiverNode) {
        return sendingConnectionsByReceiver.getOrDefault(receiverNode, List.of());
    }

    // Example of how to interact with these networks. Will remove shortly.
    private void example() {
        for (DyeColor channel : allChannels()) {
            for (var receiver : receivingConnections(channel)) {
                var extractHandler = receiver.getConnectedCapability(Capabilities.ItemHandler.BLOCK);

                // Using basic for loop to show that round robin would be fine
                var senders = sendingConnectionsFrom(receiver);
                for (int i = 0; i < senders.size(); i++) {
                    var sender = senders.get(i);
                    var insertHandler = sender.getConnectedCapability(Capabilities.ItemHandler.BLOCK);

                    // TODO: do transfers
                }
            }
        }
    }

    // endregion

    // region Context

    public boolean hasContext(ConduitNetworkContextType<?> type) {
        return context != null && context.type() == type;
    }

    @SuppressWarnings("unchecked")
    public <C extends ConduitNetworkContext<C>> @Nullable C getContext(ConduitNetworkContextType<C> type) {
        if (context != null && context.type() == type) {
            return (C) context;
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <C extends ConduitNetworkContext<C>> C getOrCreateContext(ConduitNetworkContextType<C> type) {
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
        if (shouldRebuildCache) {
            rebuildCache();
        }

        if (haveConnectionsChanged) {
            updateChannelList();
            sortConnectionLists();
        }
    }

    public void onNodeLoaded(NewConduitNode node) {
        Preconditions.checkArgument(node.isLoaded(), "Node is not loaded!");

        // If a full rebuild is scheduled, don't modify the cache
        if (shouldRebuildCache) {
            return;
        }

        addLoadedNode(node);
    }

    public void onNodeUnloaded(NewConduitNode node) {
        Preconditions.checkArgument(!node.isLoaded(), "Node is still loaded!");

        // If a full rebuild is scheduled, don't modify the cache
        if (shouldRebuildCache) {
            return;
        }

        removeLoadedNode(node);
    }

    public void onNodeUpdated(NewConduitNode node) {
        Preconditions.checkArgument(node.isLoaded(), "Node is not loaded!");

        if (shouldRebuildCache) {
            return;
        }

        // If we've somehow missed this node, do a full processing
        if (!loadedNodes.contains(node)) {
            addLoadedNode(node);
        } else {
            // TODO: We should do this with a partial edit rather than remove then add again...
            //       This will do for testing though
            removeLoadedNode(node);
            addLoadedNode(node);
        }

        // Update channel list
        updateChannelList();

        // Must re-sort in case ordering has changed.
        haveConnectionsChanged = true;
    }

    // endregion

    // region Caching Logic

    private void addLoadedNode(NewConduitNode node) {
        loadedNodes.add(node);

        for (var side : Direction.values()) {
            if (!node.isConnectedToBlock(side)) {
                continue;
            }

            var connection = new BlockConnection(node, side);
            endpointConnections.put(node, connection);

            // Add this connection to all other block connection's access maps
            for (var connectionList : accessibleBlockConnectionsMap.values()) {
                connectionList.add(connection);
            }

            // Add own list of block connection accesses.
            accessibleBlockConnectionsMap.computeIfAbsent(connection, k -> new ArrayList<>(endpointConnections.values().size()));
            for (var accessibleConnection : endpointConnections.values()) {
                if (accessibleConnection != connection) {
                    accessibleBlockConnectionsMap.get(connection).add(accessibleConnection);
                }
            }

            // Handle IO caching
            var config = node.connectionConfig(side);
            if (config instanceof IOConnectionConfig ioConnectionConfig) {
                // First add sending and receiving connections
                if (ioConnectionConfig.isSend()) {
                    sendingConnections.put(ioConnectionConfig.sendColor(), connection);
                }

                if (ioConnectionConfig.isReceive()) {
                    receivingConnections.put(ioConnectionConfig.receiveColor(), connection);
                }

                // Now handle the mappings between them, do it after both are added in case we can self-feed.
                if (ioConnectionConfig.isSend()) {
                    receivingConnectionsBySender.computeIfAbsent(connection, k -> new ArrayList<>()).addAll(receivingConnections.get(ioConnectionConfig.receiveColor()));
                }

                if (ioConnectionConfig.isReceive()) {
                    sendingConnectionsByReceiver.computeIfAbsent(connection, k -> new ArrayList<>()).addAll(sendingConnections.get(ioConnectionConfig.sendColor()));
                }
            }

            // All lists now require sorting
            haveConnectionsChanged = true;
        }
    }

    private void removeLoadedNode(NewConduitNode node) {
        loadedNodes.remove(node);

        // Remove connections from any maps
        for (var connection : endpointConnections.get(node)) {
            // Remove this connection's maps
            accessibleBlockConnectionsMap.remove(connection);

            // Not a fan of having to iterate, but it's probably fine.
            for (var color : DyeColor.values()) {
                sendingConnections.remove(color, connection);
                receivingConnections.remove(color, connection);
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
        allChannels.addAll(sendingConnections.keySet());
        allChannels.addAll(receivingConnections.keySet());
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
        loadedNodes.clear();
        endpointConnections.clear();
        accessibleBlockConnectionsMap.clear();
        sendingConnections.clear();
        receivingConnections.clear();
        sendingConnectionsByReceiver.clear();
        receivingConnectionsBySender.clear();

        // Add each loaded node into the caches.
        for (var node : nodes()) {
            if (node.isLoaded()) {
                addLoadedNode(node);
            }
        }

        // Populate channel set
        updateChannelList();

        // Sort all lists
        sortConnectionLists();

        // Rebuild complete
        shouldRebuildCache = false;
        haveConnectionsChanged = false;
    }

    private void sortConnections(BlockConnection ref, List<BlockConnection> connections) {
        connections.sort((a, b) -> conduit.value().compare(ref, a, b));
    }

    // endregion

    // region Network Impl

    @Override
    protected NewConduitNetwork createEmpty() {
        return new NewConduitNetwork(conduit);
    }

    // TODO: rather than recomputing the entire cache, I want more specific action hooks.
    @Override
    protected void onNetworkChanged() {
//        markCacheDirty();
    }

    @Override
    protected void onMerged(NewConduitNetwork other) {
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
    protected void onGraphSplit(Set<NewConduitNetwork> newNetworks) {
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
        shouldRebuildCache = true;
    }

    // endregion
}
