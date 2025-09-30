package com.enderio.enderio.machines.common.blocks.base.task;

import com.enderio.enderio.machines.common.io.energy.IMachineEnergyStorage;

public interface PoweredMachineTask extends MachineTask {
    IMachineEnergyStorage getEnergyStorage();
}
