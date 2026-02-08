package com.enderio.enderio.api.conduits.network;

public final class ConduitConnectionPath {
    private final ConduitBlockConnection start;
    private final ConduitBlockConnection end;
    private final int length;

    public ConduitConnectionPath(ConduitBlockConnection start, ConduitBlockConnection end, int length) {
        this.start = start;
        this.end = end;
        this.length = length;
    }

    public ConduitBlockConnection start() {
        return start;
    }

    public ConduitBlockConnection end() {
        return end;
    }

    public int length() {
        return length;
    }

    public ConduitConnectionPath reverse() {
        return new ConduitConnectionPath(end, start, length);
    }

    // TODO: Metadata (max speed etc.)
}
