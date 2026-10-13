package com.enderio.enderio.tests.conduits.network;

import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.connection.ConnectionStatus;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.content.conduits.network.IConduitNodeAttachment;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FakeConduitNodeAttachment implements IConduitNodeAttachment {
    @Nullable
    private final Level level;

    private final Holder<Conduit<?, ?>> conduit;

    private final Map<Direction, ResourceStorage<ItemResource>> conduitInventories = new HashMap<>();

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
    public void setConnectionConfig(Holder<Conduit<?, ?>> conduit, Direction side, ConnectionConfig config) {
        if (this.conduit != conduit) {
            throw new IllegalArgumentException("Conduit type not supported by this fake attachment");
        }

        connectionConfigs.put(side, config);
    }

    @Override
    @Nullable
    public ResourceStorage<ItemResource> getConnectionInventory(Holder<Conduit<?, ?>> conduit, Direction side) {
        if (this.conduit != conduit) {
            throw new IllegalArgumentException("Conduit type not supported by this fake attachment");
        }

        var layout = conduit.value().inventoryLayout();
        if (layout != null) {
            return conduitInventories.computeIfAbsent(side, _ -> new ItemStorage(layout));
        }

        return null;
    }
}
