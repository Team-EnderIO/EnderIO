package com.enderio.enderio.api.conduits.network;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Attempts to compare by priority (if present), then falls back on {@link DefaultConnectionComparerFromReference}.
 */
public record PriorityConnectionComparerFromReference(
    Function<ConduitBlockConnection, @Nullable Integer> priorityGetter
) implements IConnectionComparerFromReference {

    @Override
    public int compare(ConduitBlockConnection refConnection, ConduitBlockConnection connectionA, ConduitBlockConnection connectionB) {
        Integer priorityA = priorityGetter.apply(connectionA);
        Integer priorityB = priorityGetter.apply(connectionB);

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

        return DefaultConnectionComparerFromReference.INSTANCE.compare(refConnection, connectionA, connectionB);
    }
}
