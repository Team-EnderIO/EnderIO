package com.enderio.conduits.api.network.node;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.IConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * A single node in a conduit network.
 * Each node represents a single conduit within a bundle, and can have up to six connections in any {@link Direction}.
 */
@ApiStatus.AvailableSince("8.0.0")
public interface IConduitNode {
    /**
     * @return the world position of the node.
     */
    BlockPos pos();

    /**
     * @return whether the node is in a loaded chunk, and is attached correctly to its bundle.
     */
    boolean isLoaded();

    /**
     * @return whether the node is in a loaded and ticking chunk, and is attached correctly to its bundle.
     */
    boolean isTicking();

    /**
     * Mark the node as dirty, this will cause node data to be saved and will trigger a sync of any data to clients.
     */
    void markDirty();

    /**
     * @return the network this node is a member of.
     */
    IConduitNetwork getNetwork();

    /**
     * @param type the data type to check for.
     * @return whether the node has any data matching {@code type}.
     */
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
     * @return The stored data that matched this type or a new instance of the desired type.
     * @implNote If conduit data of a different type exists in this node, it will be replaced.
     */
    <T extends NodeData> T getOrCreateNodeData(NodeDataType<T> type);

    /**
     * Set the data attached to this node.
     * @param data the data to set.
     */
    <T extends NodeData> void setNodeData(@Nullable T data);

    /**
     * Get the desired capability from a neighboring block.
     *
     * @param capability the desired capability.
     * @param side the side to query for a neighboring capability.
     * @return the capability or null if it is not available.
     * @throws IllegalStateException if this node is not loaded
     */
    @Nullable
    <TCapability> TCapability getNeighborSidedCapability(BlockCapability<TCapability, Direction> capability,
            Direction side);

    /**
     * Get the desired capability from a neighboring block.
     *
     * @param capability the desired capability.
     * @param side the side to query for a neighboring capability.
     * @return the capability or null if it is not available.
     * @throws IllegalStateException if this node is not loaded
     */
    @Nullable
    <TCapability> TCapability getNeighborVoidCapability(BlockCapability<TCapability, Void> capability, Direction side);

    /**
     * @param signalColor the redstone conduit signal color to check for, or null for in-world signal only.
     * @return whether there is a redstone signal.
     * @throws IllegalStateException if this node is not loaded
     */
    boolean hasRedstoneSignal(@Nullable DyeColor signalColor);

    /**
     * @param side side to check for.
     * @return whether this node is connected to a block on this {@code side}.
     * @throws IllegalStateException if this node is not loaded
     */
    boolean isConnectedToBlock(Direction side);

    /**
     * @param side side to check for.
     * @return whether this node is connected to a block or another conduit on this {@code side}.
     * @throws IllegalStateException if this node is not loaded
     */
    boolean isConnectedTo(Direction side);

    /**
     * @param side the connection to query.
     * @return the configuration for this connection, untyped.
     * @throws IllegalStateException if this node is not loaded
     */
    ConnectionConfig getConnectionConfig(Direction side);

    /**
     * @param side the connection to query.
     * @param type the type of configuration to get.
     * @return the configuration for this connection.
     * @throws IllegalStateException if this node is not loaded
     * @throws IllegalStateException if the type requested does not match the stored type.
     */
    <T extends ConnectionConfig> T getConnectionConfig(Direction side, ConnectionConfigType<T> type);

    /**
     * @param side connection side to get an inventory for.
     * @return the inventory for the desired connection.
     * @throws IllegalStateException if this node is not loaded
     * @throws IllegalStateException if this node has no inventory.
     */
    IItemHandlerModifiable getInventory(Direction side);
}
