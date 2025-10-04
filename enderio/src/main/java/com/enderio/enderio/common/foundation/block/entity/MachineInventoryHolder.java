package com.enderio.enderio.common.foundation.block.entity;

import com.enderio.enderio.common.foundation.inventory.MachineInventory;

public interface MachineInventoryHolder {
    boolean hasInventory();

    MachineInventory getInventory();
}
