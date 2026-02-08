package com.enderio.enderio.api.conduits.connection.path;

import java.util.Comparator;

public enum DefaultConnectionPathComparator implements Comparator<ConduitConnectionPath> {
    INSTANCE;

    @Override
    public int compare(ConduitConnectionPath pathA, ConduitConnectionPath pathB) {
        // TODO: Switch to path length at some point. Maintaining current operation for now.
//        return Integer.compare(pathA.length(), pathB.length());

        return Integer.compare(pathA.start().connectedBlockPos().distManhattan(pathA.end().connectedBlockPos()),
            pathB.start().connectedBlockPos().distManhattan(pathB.end().connectedBlockPos()));
    }
}
