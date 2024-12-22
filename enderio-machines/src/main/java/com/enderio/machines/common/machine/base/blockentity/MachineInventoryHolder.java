package com.enderio.machines.common.machine.base.blockentity;

import com.enderio.machines.common.machine.base.inventory.MachineInventory;

public interface MachineInventoryHolder {
    boolean hasInventory();

    MachineInventory getInventory();
}
