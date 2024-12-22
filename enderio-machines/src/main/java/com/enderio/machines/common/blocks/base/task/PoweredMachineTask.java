package com.enderio.machines.common.blocks.base.task;

import com.enderio.machines.common.io.energy.IMachineEnergyStorage;

public interface PoweredMachineTask extends MachineTask {
    IMachineEnergyStorage getEnergyStorage();
}
