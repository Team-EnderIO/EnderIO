package com.enderio.enderio.api.conduits.connection;

import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a connection between a node and a block.
 *
 * @param node           The node that is connected to the block.
 * @param connectionSide The direction of the connection from the node.
 */
@ApiStatus.AvailableSince("8.0.0")
@ApiStatus.Experimental
public record ConduitBlockConnection(ConduitNode node, Direction connectionSide) {

    /**
     * @return the position of the connected block.
     */
    public BlockPos connectedBlockPos() {
        return node.pos().relative(connectionSide);
    }

    /**
     * Get the desired capability from the connected block.
     *
     * @param capability the desired capability.
     * @return the capability or null if it is not available.
     */
    @Nullable
    public <T, C> T getCapability(BlockCapability<T, C> capability, @Nullable C context) {
        return node.getCapability(capability, connectionSide, context);
    }

    /**
     * Get the desired capability from the connected block.
     *
     * @param capability the desired capability.
     * @return the capability or null if it is not available.
     */
    @Nullable
    public <T> T getSidedCapability(BlockCapability<T, Direction> capability) {
        return getCapability(capability, connectionSide.getOpposite());
    }

    /**
     * @return the configuration for this connection, untyped.
     */
    public ConnectionConfig connectionConfig() {
        return node.getConnectionConfig(connectionSide);
    }

    /**
     * @param type the type of configuration to get.
     * @return the configuration for this connection.
     * @throws IllegalStateException if the type requested does not match the stored type.
     */
    public <T extends ConnectionConfig> T connectionConfig(ConnectionConfigType<T> type) {
        return node.getConnectionConfig(connectionSide, type);
    }

    /**
     * @return the inventory for this conduit connection.
     */
    public IItemHandlerModifiable inventory() {
        return node.getInventory(connectionSide);
    }
}
