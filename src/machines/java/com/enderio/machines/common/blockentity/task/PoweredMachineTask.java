package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.io.energy.IMachineEnergyStorage;

public interface PoweredMachineTask extends MachineTask {
    IMachineEnergyStorage getEnergyStorage();
}
