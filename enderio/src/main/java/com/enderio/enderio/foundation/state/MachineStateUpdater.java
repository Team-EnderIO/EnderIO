package com.enderio.enderio.foundation.state;

@FunctionalInterface
public interface MachineStateUpdater {
    void updateMachineState(MachineState state, boolean add);
}
