package com.enderio.conduits.common.blockentity.connection;

public sealed interface IConnectionState permits StaticConnectionStates, DynamicConnectionState {

    boolean isConnection();
}
