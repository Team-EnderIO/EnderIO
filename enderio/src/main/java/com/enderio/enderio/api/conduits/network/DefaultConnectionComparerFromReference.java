package com.enderio.enderio.api.conduits.network;

public enum DefaultConnectionComparerFromReference implements IConnectionComparerFromReference {
    INSTANCE;

    @Override
    public int compare(ConduitBlockConnection refConnection, ConduitBlockConnection connectionA, ConduitBlockConnection connectionB) {
        return Integer.compare(refConnection.connectedBlockPos().distManhattan(connectionA.connectedBlockPos()),
            refConnection.connectedBlockPos().distManhattan(connectionB.connectedBlockPos()));
    }
}
