package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.io.energy.IMachineEnergyStorage;

public interface IPoweredMachineTask extends IMachineTask {
    IMachineEnergyStorage getEnergyStorage();
}
