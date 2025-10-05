package com.enderio.enderio.foundation.task;

import com.enderio.enderio.foundation.io.energy.IMachineEnergyStorage;

public interface PoweredMachineTask extends MachineTask {
    IMachineEnergyStorage getEnergyStorage();
}
