package com.enderio.machines.common.rewrite.blockentity;

import com.enderio.machines.common.io.item.MachineInventory;

public interface MachineInventoryHolder {
    boolean hasInventory();
    MachineInventory getInventory();
}
