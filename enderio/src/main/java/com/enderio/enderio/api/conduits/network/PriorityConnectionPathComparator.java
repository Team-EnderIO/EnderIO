package com.enderio.enderio.api.conduits.network;

import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.function.Function;

/**
 * Attempts to compare by priority (if present), then falls back on {@link DefaultConnectionPathComparator}.
 */
public record PriorityConnectionPathComparator(
    Function<ConduitBlockConnection, @Nullable Integer> priorityGetter
) implements Comparator<ConduitConnectionPath> {

    @Override
    public int compare(ConduitConnectionPath pathA, ConduitConnectionPath pathB) {
        // We use INPUT priority.
        // TODO: Make this more clear.
        Integer priorityA = priorityGetter.apply(pathA.end());
        Integer priorityB = priorityGetter.apply(pathB.end());

        // If one priority is null and the other isn't, sort the non-null one above the null one.
        if (priorityA == null && priorityB != null) {
            return 1; // A after B
        }
        if (priorityA != null && priorityB == null) {
            return -1; // A before B
        }

        // If both are present, compare (higher priority first).
        if (priorityA != null && !priorityA.equals(priorityB)) {
            return Integer.compare(priorityB, priorityA);
        }

        return DefaultConnectionPathComparator.INSTANCE.compare(pathA, pathB);
    }
}
