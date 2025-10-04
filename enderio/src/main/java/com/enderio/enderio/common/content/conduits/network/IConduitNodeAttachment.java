package com.enderio.enderio.common.content.conduits.network;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

// Interface for attaching a node to the level.
// Added so that we can perform tests on ConduitNode and ConduitNetwork.
public interface IConduitNodeAttachment {
    boolean hasLevel();

    @Nullable
    Level getLevel();

    void markNodesDirty();

    @Nullable
    <TCapability> TCapability getNeighborSidedCapability(Holder<Conduit<?, ?>> conduit,
        BlockCapability<TCapability, Direction> capability, Direction side);

    @Nullable
    <TCapability> TCapability getNeighborVoidCapability(Holder<Conduit<?, ?>> conduit,
        BlockCapability<TCapability, Void> capability, Direction side);

    boolean hasRedstoneSignal(@Nullable DyeColor signalColor);

    ConnectionStatus getConnectionStatus(Holder<Conduit<?, ?>> conduit, Direction side);

    ConnectionConfig getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side);

    <T extends ConnectionConfig> T getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfigType<T> type);

    void setConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfig config);

    @Nullable
    IItemHandlerModifiable getConnectionInventory(Holder<Conduit<?, ?>> conduit, Direction side);
}
