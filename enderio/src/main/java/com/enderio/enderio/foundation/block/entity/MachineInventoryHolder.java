package com.enderio.enderio.foundation.block.entity;

import com.enderio.core.common.storage.ItemStorage;

public interface MachineInventoryHolder {
    boolean hasInventory();

    ItemStorage getInventory();
}
