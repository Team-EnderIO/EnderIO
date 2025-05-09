package com.enderio.conduits.common.conduit.new_graph;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public record BlockConnection(NewConduitNode node, Direction connectionSide) {

    // TODO: we might want support for Void block capabilities...

    public BlockPos blockPos() {
        return node.pos().relative(connectionSide);
    }

    public <TCapability> TCapability getConnectedCapability(BlockCapability<TCapability, Direction> capability) {
        return node.getCapabilityAtNeighbor(capability, connectionSide);
    }

    public ConnectionConfig connectionConfig() {
        return node.connectionConfig(connectionSide);
    }

    public <T extends ConnectionConfig> T connectionConfig(ConnectionConfigType<T> type) {
        return node.connectionConfig(connectionSide, type);
    }

    public void setConnectionConfig(ConnectionConfig connectionConfig) {
        node.setConnectionConfig(connectionSide, connectionConfig);
    }

    public IItemHandlerModifiable getInventory() {
        return node.getInventory(connectionSide);
    }
}
