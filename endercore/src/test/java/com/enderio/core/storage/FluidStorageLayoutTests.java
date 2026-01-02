package com.enderio.core.storage;

import com.enderio.core.common.storage.FluidStorageLayout;
import com.enderio.core.common.storage.MultiResourceSlotKey;
import com.enderio.core.common.storage.SingleResourceSlotKey;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FluidStorageLayoutTests {
    @Test
    public void ensureNoSlotCapacityThrowsOnSingleSlot() {
        var key = new SingleResourceSlotKey<FluidResource>();
        Assertions.assertThrows(IllegalStateException.class, () -> FluidStorageLayout.<Void>builder().inputSlot(key).build());
    }

    @Test
    public void ensureNoSlotCapacityThrowsOnMultipleSlots() {
        var key = new MultiResourceSlotKey<FluidResource>(3);
        Assertions.assertThrows(IllegalStateException.class, () -> FluidStorageLayout.<Void>builder().inputSlots(key).build());
    }
}
