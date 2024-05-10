package com.enderio.conduits.common.blockentity.connection;

public sealed interface ConnectionState permits StaticConnectionStates, DynamicConnectionState {
    boolean isConnection();
}
