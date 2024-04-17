package com.enderio.api.conduit.connection;

public sealed interface IConnectionState permits StaticConnectionStates, DynamicConnectionState {

    boolean isConnection();
}
