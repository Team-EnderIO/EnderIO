package com.enderio.enderio.common.foundation.task;

import com.enderio.enderio.common.foundation.io.energy.IMachineEnergyStorage;

public interface PoweredMachineTask extends MachineTask {
    IMachineEnergyStorage getEnergyStorage();
}
