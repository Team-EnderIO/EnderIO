package com.enderio.conduits.common.blockentity.connection;

/**
 * This class is for all static ConnectionStates without being connected to a block. For this use {@link DynamicConnectionState} <br>
 * It's intended usage is to remove the full nullability things of ConnectionStates
 * {@linkplain StaticConnectionStates#CONNECTED CONNECTED} is for all conduits that extends in a direction, but are not showing a connector
 * {@linkplain StaticConnectionStates#CONNECTED_ACTIVE CONNECTED_ACTIVE} is for all conduits that extends in a direction, but are not showing a connector and should use their active texture and emissiveness
 * {@linkplain StaticConnectionStates#DISCONNECTED DISCONNECTED} is for no Connection
 * {@linkplain StaticConnectionStates#DISABLED DISABLED} is for no connection and activly disabled, so they don't connect, when a conduit is placed next to it (this state is set using the wrench or when IO is both disabled the state returns to this)
 */
public enum StaticConnectionStates implements IConnectionState {
    CONNECTED,
    CONNECTED_ACTIVE,
    DISCONNECTED,
    DISABLED;

    @Override
    public boolean isConnection() {
        return this == CONNECTED || this == CONNECTED_ACTIVE;
    }
}
