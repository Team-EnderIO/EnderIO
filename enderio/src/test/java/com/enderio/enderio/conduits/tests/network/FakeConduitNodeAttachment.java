package com.enderio.enderio.conduits.tests.network;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.content.conduits.network.IConduitNodeAttachment;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FakeConduitNodeAttachment implements IConduitNodeAttachment {
    @Nullable
    private final Level level;

    private final Holder<Conduit<?, ?>> conduit;

    private final Map<Direction, IItemHandlerModifiable> conduitInventories = new HashMap<>();

    private final Map<Direction, ConnectionStatus> connectionStatuses = new HashMap<>();
    private final Map<Direction, ConnectionConfig> connectionConfigs = new HashMap<>();

    private FakeConduitNodeAttachment(@Nullable Level level, Holder<Conduit<?, ?>> conduit) {
        this.level = level;
        this.conduit = conduit;
    }

    @Override
    public boolean hasLevel() {
        return level != null;
    }

    @Override
    @Nullable
    public Level getLevel() {
        return level;
    }

    @Override
    public void markNodesDirty() {
        // no op
    }

    @Override
    public @Nullable <T, C> T getCapability(Holder<Conduit<?, ?>> conduit, BlockCapability<T, C> capability, Direction neighborSide, @Nullable C context) {
        return null;
    }

    @Override
    public boolean hasRedstoneSignal(@Nullable DyeColor signalColor) {
        return false;
    }

    @Override
    public ConnectionStatus getConnectionStatus(Holder<Conduit<?, ?>> conduit, Direction side) {
        if (this.conduit != conduit) {
            throw new IllegalArgumentException("Conduit type not supported by this fake attachment");
        }

        return connectionStatuses.computeIfAbsent(side, s -> ConnectionStatus.DISCONNECTED);
    }

    public void setConnectionStatus(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionStatus connectionStatus) {
        if (this.conduit != conduit) {
            throw new IllegalArgumentException("Conduit type not supported by this fake attachment");
        }

        connectionStatuses.put(side, connectionStatus);
    }

    @Override
    public ConnectionConfig getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side) {
        if (this.conduit != conduit) {
            throw new IllegalArgumentException("Conduit type not supported by this fake attachment");
        }

        return connectionConfigs.computeIfAbsent(side, s -> conduit.value().type().connectionConfigType().getDefault());
    }

    @Override
    public <T extends ConnectionConfig> T getConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfigType<T> type) {
        if (this.conduit != conduit) {
            throw new IllegalArgumentException("Conduit type not supported by this fake attachment");
        }

        var config = connectionConfigs.computeIfAbsent(side, s -> type.getDefault());
        if (config.type() != type) {
            config = type.getDefault();
            connectionConfigs.put(side, config);
        }

        //noinspection unchecked
        return (T)config;
    }

    @Override
    public void setConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfig config) {
        if (this.conduit != conduit) {
            throw new IllegalArgumentException("Conduit type not supported by this fake attachment");
        }

        connectionConfigs.put(side, config);
    }

    @Override
    @Nullable
    public IItemHandlerModifiable getConnectionInventory(Holder<Conduit<?, ?>> conduit, Direction side) {
        if (this.conduit != conduit) {
            throw new IllegalArgumentException("Conduit type not supported by this fake attachment");
        }

        if (conduit.value().getInventorySize() > 0) {
            return conduitInventories.computeIfAbsent(side, s -> new ConnectionInventory());
        }

        return null;
    }

    private class ConnectionInventory extends ItemStackHandler {
        public ConnectionInventory() {
            super(conduit.value().getInventorySize());
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return conduit.value().isItemValid(slot, stack);
        }
    }
}
