package com.enderio.conduits.api.network.node;

import com.enderio.conduits.api.connection.config.ConnectionConfigAccessor;
import com.enderio.conduits.api.network.ConduitNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

public interface ConduitNode extends NodeDataAccessor, ConnectionConfigAccessor {
    /**
     * @throws IllegalStateException if the node is not loaded in the world.
     * @return the world position of the node.
     */
    BlockPos getPos();

    /**
     * @throws IllegalStateException if the node is not loaded in the world, or if the conduit has no inventory.
     * @param side
     * @return
     */
    IItemHandlerModifiable getInventory(Direction side);

    // TODO: investigate nullability for this interface?
    @Nullable
    ConduitNetwork getNetwork();

    /**
     * Get a capability for the given side of the node
     */
    @Nullable
    <TCapability> TCapability getNeighbourCapability(BlockCapability<TCapability, Direction> capability, Direction side);

    /**
     * @return whether this node's bundle is loaded and ticking in the world
     */
    boolean isLoaded();

    /**
     * Check whether there is a redstone signal to this node's bundle.
     * @param channelColor
     * @return
     */
    boolean hasRedstoneSignal(@Nullable DyeColor channelColor);

    /**
     * Mark the node as dirty, causing the owning bundle to save and sync.
     */
    void markDirty();
}
