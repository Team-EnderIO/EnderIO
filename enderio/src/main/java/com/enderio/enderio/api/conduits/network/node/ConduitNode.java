package com.enderio.enderio.api.conduits.network.node;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitCapabilityAccessor;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.ConnectionReader;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.ConduitNetwork;
import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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
public interface ConduitNode extends ConduitCapabilityAccessor, ConnectionReader {
    /**
     * @return the conduit the node represents.
     * @apiNote Currently the node must be valid (loaded, attached etc.) for this to not throw an exception.
     */
    @ApiStatus.Experimental
    Holder<Conduit<?, ?>> conduit();

    /**
     * @param type the expected type of conduit
     * @return the conduit the node represents.
     * @param <T> the type of conduit
     * @throws IllegalStateException if the node is not valid or the conduit is not of the expected type
     */
    @SuppressWarnings("unchecked")
    @ApiStatus.NonExtendable
    default <T extends Conduit<T, ?>> Holder<T> conduit(ConduitType<T, ?> type) {
        var conduitHolderUntyped = conduit();
        Preconditions.checkState(conduitHolderUntyped.value().type() == type);
        return (Holder<T>) conduitHolderUntyped;
    }

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
    ConduitNetwork getNetwork();

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
     * @throws IllegalStateException if this node is not loaded
     */
    @Override
    @Nullable <T, C> T getCapability(BlockCapability<T, C> capability, Direction neighborSide, @Nullable C context);

    /**
     * @throws IllegalStateException if this node is not loaded
     */
    @Override
    @Nullable
    default <T> T getSidedCapability(BlockCapability<T, Direction> capability, Direction neighborSide) {
        return ConduitCapabilityAccessor.super.getSidedCapability(capability, neighborSide);
    }

    /**
     * @param signalColor the redstone conduit signal color to check for, or null for in-world signal only.
     * @return whether there is a redstone signal.
     * @throws IllegalStateException if this node is not loaded
     */
    boolean hasRedstoneSignal(@Nullable DyeColor signalColor);

    /**
     * @param side connection side to get an inventory for.
     * @return the inventory for the desired connection.
     * @throws IllegalStateException if this node is not loaded
     * @throws IllegalStateException if this node has no inventory.
     */
    IItemHandlerModifiable getInventory(Direction side);
}
