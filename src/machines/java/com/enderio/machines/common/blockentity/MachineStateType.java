package com.enderio.machines.common.blockentity;

public enum MachineStateType {

    ACTIVE(0),
    IDLE(1),
    ERROR(2),
    DISABLED(3);

    private final int priority;
    MachineStateType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
