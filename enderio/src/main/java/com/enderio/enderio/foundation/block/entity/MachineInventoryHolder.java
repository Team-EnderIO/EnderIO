package com.enderio.enderio.foundation.block.entity;

import com.enderio.enderio.foundation.inventory.MachineInventory;

public interface MachineInventoryHolder {
    boolean hasInventory();

    MachineInventory getInventory();
}
