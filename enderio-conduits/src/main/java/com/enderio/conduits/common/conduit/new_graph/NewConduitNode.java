package com.enderio.conduits.common.conduit.new_graph;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.network.node.NodeData;
import com.enderio.conduits.api.network.node.NodeDataType;
import com.enderio.conduits.common.conduit.graph.ConduitConnectionHost;
import com.enderio.core.common.graph.INetworkNode;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public class NewConduitNode<T extends ConnectionConfig> implements INetworkNode<NewConduitNetwork<T>, NewConduitNode<T>> {

    // TODO: Add serialization and legacy data conversion.

    private BlockPos pos;
    @Nullable private NodeData nodeData;

    @Nullable private NewConduitNetwork<T> network;
    @Nullable private ConduitConnectionHost connectionHost;

    public BlockPos pos() {
        return pos;
    }

    public boolean isLoaded() {
        return connectionHost != null && connectionHost.isLoaded();
    }

    public void markDirty() {
        ensureValid();
        //noinspection DataFlowIssue
        connectionHost.onNodeDirty();
    }

    // region Node Data

    public boolean hasData(NodeDataType<?> type) {
        return nodeData != null && nodeData.type() == type;
    }

    @Nullable
    public NodeData getNodeData() {
        return nodeData;
    }

    @Nullable
    public <D extends NodeData> D getNodeData(NodeDataType<D> type) {
        if (nodeData != null && type == nodeData.type()) {
            //noinspection unchecked
            return (D) nodeData;
        }

        return null;
    }

    public <D extends NodeData> D getOrCreateNodeData(NodeDataType<D> type) {
        if (nodeData != null && type == nodeData.type()) {
            // noinspection unchecked
            return (D) nodeData;
        }

        nodeData = type.factory().get();
        // noinspection unchecked
        return (D) nodeData;
    }

    public <D extends NodeData> void setNodeData(@Nullable D data) {
        nodeData = data;
    }

    // endregion

    // region World Interaction

    public <TCapability> TCapability getCapabilityAtNeighbor(BlockCapability<TCapability, Direction> capability, Direction side) {
        ensureValid();
        //noinspection DataFlowIssue
        return connectionHost.getNeighbourCapability(capability, side);
    }

    public boolean hasRedstoneSignal(@Nullable DyeColor signalColor) {
        ensureValid();
        //noinspection DataFlowIssue
        return connectionHost.hasRedstoneSignal(signalColor);
    }

    // endregion

    // region Connections

    public boolean isConnectedToBlock(Direction side) {
        ensureValid();
        //noinspection DataFlowIssue
        return connectionHost.isConnectedToBlock(side);
    }

    public boolean isConnectedTo(Direction side) {
        ensureValid();
        //noinspection DataFlowIssue
        return connectionHost.isConnectedTo(side);
    }

    public T connectionConfig(Direction side) {
        ensureValid();
        //noinspection DataFlowIssue
        return connectionHost.getConnectionConfig(side, network.connectionConfigType());
    }

    public void setConnectionConfig(Direction side, T connectionConfig) {
        ensureValid();
        //noinspection DataFlowIssue
        connectionHost.setConnectionConfig(side, connectionConfig);
    }

    // endregion

    // region Inventory

    public IItemHandlerModifiable getInventory(Direction side) {
        ensureValid();

        // We don't have to do this, but it saves null checks in the tickers.
        // Only tickers that know they have inventories should use this anyway.
        //noinspection DataFlowIssue
        var inventory = connectionHost.getInventory(side);
        if (inventory == null) {
            throw new IllegalStateException("This conduit does not have an inventory!");
        }

        return inventory;
    }

    // endregion

    // region Network Node Impl

    @Override
    @Nullable
    public NewConduitNetwork<T> getNetwork() {
        return network;
    }

    @Override
    public void setNetwork(@Nullable NewConduitNetwork<T> network) {
        this.network = network;
    }

    // endregion

    private void ensureValid() {
        Preconditions.checkState(network != null, "Conduit node is not connected to a network.");
        Preconditions.checkState(connectionHost != null, "Conduit node is not attached to the level yet.");
        Preconditions.checkState(connectionHost.isLoaded(), "Conduit node is attached but is in an unloaded chunk or ticking is paused..");
    }
}
