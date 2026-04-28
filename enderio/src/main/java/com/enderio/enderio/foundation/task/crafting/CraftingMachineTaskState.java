package com.enderio.enderio.foundation.task.crafting;

public enum CraftingMachineTaskState {
    ACTIVE,
    OUTPUT_BLOCKED,
    CANCELLED,
    COMPLETED;

    public boolean shouldStop() {
        return this == CANCELLED || this == COMPLETED;
    }
}
