package com.enderio.enderio.foundation.task;

import com.enderio.enderio.foundation.energy.MachineEnergyHandler;

public interface PoweredMachineTask extends MachineTask {
    MachineEnergyHandler getEnergyStorage();
}
