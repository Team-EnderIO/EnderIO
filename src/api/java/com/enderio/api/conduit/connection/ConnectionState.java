package com.enderio.api.conduit.connection;

public sealed interface ConnectionState permits StaticConnectionStates, DynamicConnectionState {

    boolean isConnection();
}
