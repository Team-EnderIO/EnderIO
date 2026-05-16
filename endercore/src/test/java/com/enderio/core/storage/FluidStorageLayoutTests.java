package com.enderio.core.storage;

import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ResourceStorageLayout;
import com.enderio.core.common.storage.layout.SimpleSlotAccessRules;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.UnaryOperator;

public class FluidStorageLayoutTests {
    @Test
    public void ensureNoSlotCapacityThrowsOnSingleSlot() {
        var key = new SingleResourceSlotKey<FluidResource>();
        Assertions.assertThrows(IllegalStateException.class, () -> FluidStorageLayout.builder().add(key, input()).build());
    }

    @Test
    public void ensureNoSlotCapacityThrowsOnMultipleSlots() {
        var key = new MultiResourceSlotKey<FluidResource>(3);
        Assertions.assertThrows(IllegalStateException.class, () -> FluidStorageLayout.builder().add(key, input()).build());
    }

    private static <T extends Resource> UnaryOperator<ResourceStorageLayout.Builder.SlotBuilder<T>> input() {
        return builder -> builder
            .externalRules(new SimpleSlotAccessRules<>(true, false))
            .guiRules(new SimpleSlotAccessRules<>(true, true));
    }
}
