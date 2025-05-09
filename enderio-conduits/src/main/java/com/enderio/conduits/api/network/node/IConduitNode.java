package com.enderio.conduits.api.network.node;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.IConduitNetwork;
import com.enderio.core.common.graph.INetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.AvailableSince("8.0")
public interface IConduitNode {
    BlockPos pos();
    boolean isLoaded();
    void markDirty();

    boolean hasNodeData(NodeDataType<?> type);

    /**
     * Get the data attached to this node, with no preferred type.
     * @return The attached data or null if there is no data.
     */
    @Nullable
    NodeData getNodeData();

    /**
     * Get the data attached to this node.
     * @param type The expected data type.
     * @return The attached data or null if there is no data -or- the data is of a different type.
     */
    @Nullable
    <T extends NodeData> T getNodeData(NodeDataType<T> type);

    /**
     * Gets the data attached to this node.
     * @param type The data type that is expected.
     * @return The stored data that matched this type, or a new instance of the data.
     * @implNote If a conduit data of a different type exists in this node, it will be replaced.
     */
    <T extends NodeData> T getOrCreateNodeData(NodeDataType<T> type);

    /**
     * Set the data attached to this node.
     *
     * @param data
     * @param <T>
     */
    <T extends NodeData> void setNodeData(@Nullable T data);

    @Nullable
    <TCapability> TCapability getCapabilityAtNeighbor(BlockCapability<TCapability, Direction> capability, Direction side);

    boolean hasRedstoneSignal(@Nullable DyeColor signalColor);

    boolean isConnectedToBlock(Direction side);

    boolean isConnectedTo(Direction side);

    ConnectionConfig getConnectionConfig(Direction side);
    <T extends ConnectionConfig> T getConnectionConfig(Direction side, ConnectionConfigType<T> type);
    void setConnectionConfig(Direction side, ConnectionConfig config);

    IItemHandlerModifiable getInventory(Direction side);
}
