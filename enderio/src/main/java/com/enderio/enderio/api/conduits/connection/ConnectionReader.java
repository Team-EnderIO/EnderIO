package com.enderio.enderio.api.conduits.connection;

import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public interface ConnectionReader {
    /**
     * Get the connection status for a given side.
     * @param side side to check for.
     * @return the connection status.
     */
    ConnectionStatus getConnectionStatus(Direction side);

    /**
     * @param side side to check for.
     * @return whether this node is connected to a block on this {@code side}.
     * @throws IllegalStateException if this node is not loaded
     */
    @ApiStatus.NonExtendable
    default boolean isConnectedToBlock(Direction side) {
        return getConnectionStatus(side).isEndpoint();
    }

    /**
     * @param side side to check for.
     * @return whether this node is connected to a block or another conduit on this {@code side}.
     * @throws IllegalStateException if this node is not loaded
     */
    @ApiStatus.NonExtendable
    default boolean isConnectedTo(Direction side) {
        return getConnectionStatus(side).isConnected();
    }

    /**
     * Get the connection config for the given side (generic).
     *
     * @param side the side to query.
     * @return the connection config.
     * @throws IllegalArgumentException if there is no connection on this side.
     */
    ConnectionConfig getConnectionConfig(Direction side);

    /**
     * Get the connection config for the given side (specific).
     *
     * @param side the side to query.
     * @param type the type of connection config to get.
     * @return the connection config.
     * @throws IllegalArgumentException if there is no connection on this side.
     * @throws IllegalStateException if the connection type does not match the requested type.
     */
    @ApiStatus.NonExtendable
    default <T extends ConnectionConfig> T getConnectionConfig(Direction side, ConnectionConfigType<T> type) {
        var config = getConnectionConfig(side);
        if (config.type() != type) {
            throw new IllegalStateException("Connection config type mismatch.");
        }

        // noinspection unchecked
        return (T) config;
    }
}
