package com.enderio.machines.common.blockentity;

public enum MachineStateType {

    READY(0),
    ERROR(1),
    USER_INPUT(2);

    private final int priority;
    MachineStateType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
