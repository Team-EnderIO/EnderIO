package com.enderio.core.storage;

import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FluidStorageLayoutTests {
    @Test
    public void ensureNoSlotCapacityThrowsOnSingleSlot() {
        var key = new SingleResourceSlotKey<FluidResource>();
        Assertions.assertThrows(IllegalStateException.class, () -> FluidStorageLayout.<Void>builder().slot(key, SlotTemplates.input()).build());
    }

    @Test
    public void ensureNoSlotCapacityThrowsOnMultipleSlots() {
        var key = new MultiResourceSlotKey<FluidResource>(3);
        Assertions.assertThrows(IllegalStateException.class, () -> FluidStorageLayout.<Void>builder().slots(key, SlotTemplates.input()).build());
    }
}
