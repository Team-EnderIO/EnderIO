package com.enderio.conduits.common.conduit.graph;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

// TODO: Did the interface for now but honestly maybe this should just be an object because its not API public.
public interface ConduitConnectionHost {
    Holder<Conduit<?, ?>> conduit();

    BlockPos pos();

    /**
     * Get a capability for the given side of the node
     */
    @Nullable
    <TCapability> TCapability getNeighbourCapability(BlockCapability<TCapability, Direction> capability, Direction side);

    boolean isConnectedToBlock(Direction side);

    boolean isConnectedTo(Direction side);

    ConnectionConfig getConnectionConfig(Direction side);

    void setConnectionConfig(Direction side, ConnectionConfig connectionConfig);

    @Nullable
    IItemHandlerModifiable getInventory(Direction side);

    void onNodeDirty();

    boolean isLoaded();

    boolean hasRedstoneSignal(@Nullable DyeColor signalColor);
}
