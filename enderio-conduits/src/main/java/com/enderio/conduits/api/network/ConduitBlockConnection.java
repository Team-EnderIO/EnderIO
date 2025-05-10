package com.enderio.conduits.api.network;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.network.node.IConduitNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.AvailableSince("8.0")
@ApiStatus.Experimental
public record ConduitBlockConnection(IConduitNode node, Direction connectionSide) {

    // TODO: we might want support for Void block capabilities...

    public BlockPos nodePos() {
        return node.pos();
    }

    public BlockPos connectedBlockPos() {
        return node.pos().relative(connectionSide);
    }

    @Nullable
    public <TCapability> TCapability getConnectedCapability(BlockCapability<TCapability, Direction> capability) {
        return node.getCapabilityAtNeighbor(capability, connectionSide);
    }

    public ConnectionConfig connectionConfig() {
        return node.getConnectionConfig(connectionSide);
    }

    public <T extends ConnectionConfig> T connectionConfig(ConnectionConfigType<T> type) {
        return node.getConnectionConfig(connectionSide, type);
    }

    public void setConnectionConfig(ConnectionConfig connectionConfig) {
        node.setConnectionConfig(connectionSide, connectionConfig);
    }

    public IItemHandlerModifiable getInventory() {
        return node.getInventory(connectionSide);
    }
}
