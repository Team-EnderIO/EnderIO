package com.enderio.enderio.api.farm;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public enum FarmTaskType {
    PLANT(1),
    FERTILIZE(3),
    HARVEST(5);

    private final int priority;

    FarmTaskType(int priority) {
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }
}
