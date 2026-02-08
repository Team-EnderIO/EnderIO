package com.enderio.enderio.api.conduits.connection.path;

import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;

/**
 * Sorts connection paths by block distance from the start to end connected block.
 */
@ApiStatus.AvailableSince("8.1.0")
public enum DefaultConnectionPathComparator implements Comparator<ConduitConnectionPath> {
    INSTANCE;

    @Override
    public int compare(ConduitConnectionPath pathA, ConduitConnectionPath pathB) {
        // If paths are the same length, fall back onto physical distance of blocks.
        if (pathA.length() == pathB.length()) {
            return Integer.compare(pathA.start().connectedBlockPos().distManhattan(pathA.end().connectedBlockPos()),
                pathB.start().connectedBlockPos().distManhattan(pathB.end().connectedBlockPos()));
        }

        return Integer.compare(pathA.length(), pathB.length());
    }
}
