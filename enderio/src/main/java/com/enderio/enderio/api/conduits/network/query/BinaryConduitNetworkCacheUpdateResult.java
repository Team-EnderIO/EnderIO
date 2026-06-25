package com.enderio.enderio.api.conduits.network.query;

public enum BinaryConduitNetworkCacheUpdateResult implements ConduitNetworkCacheUpdateResult {
    NO_CHANGE(false),
    CHANGED(true);

    private final boolean didChange;

    BinaryConduitNetworkCacheUpdateResult(boolean didChange) {
        this.didChange = didChange;
    }

    @Override
    public boolean didChange() {
        return didChange;
    }
}
