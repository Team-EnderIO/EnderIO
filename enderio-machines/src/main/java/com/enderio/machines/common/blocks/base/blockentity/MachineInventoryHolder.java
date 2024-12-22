package com.enderio.machines.common.blocks.base.blockentity;

import com.enderio.machines.common.blocks.base.inventory.MachineInventory;

public interface MachineInventoryHolder {
    boolean hasInventory();

    MachineInventory getInventory();
}
