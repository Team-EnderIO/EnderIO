package com.enderio.conduits.common.conduit.connection;

public sealed interface ConnectionState permits StaticConnectionStates, DynamicConnectionState {

    boolean isConnection();
}
