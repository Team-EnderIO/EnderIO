package com.enderio.core.util;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderResourceUtil;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EnderResourceUtilTests {

    // region moveInto tests

    @Test
    public void testMoveInto_Success() {
        // Create source storage with 2 slots containing items
        var sourceSlot1 = new SingleResourceSlotKey<ItemResource>();
        var sourceSlot2 = new SingleResourceSlotKey<ItemResource>();
        var sourceLayout = ItemStorageLayout.builder()
            .add(sourceSlot1, SlotTemplates.storage())
            .add(sourceSlot2, SlotTemplates.storage())
            .build();
        ItemStorage sourceStorage = new ItemStorage(sourceLayout);
        sourceStorage.set(0, ItemResource.of(Items.DIAMOND), 20);
        sourceStorage.set(1, ItemResource.of(Items.EMERALD), 30);

        // Create destination storage with 2 empty slots
        var destSlot1 = new SingleResourceSlotKey<ItemResource>();
        var destSlot2 = new SingleResourceSlotKey<ItemResource>();
        var destLayout = ItemStorageLayout.builder()
            .add(destSlot1, SlotTemplates.storage())
            .add(destSlot2, SlotTemplates.storage())
            .build();
        ItemStorage destStorage = new ItemStorage(destLayout);

        // Move up to 15 diamonds from source to destination slot 0
        int amountMoved = EnderResourceUtil.moveInto(
            sourceStorage,
            destStorage,
            0,
            resource -> true, // Accept all items
            15,
            null
        );

        // Assert correct amount was moved
        Assertions.assertEquals(15, amountMoved, "Should move 15 diamonds");

        // Check source storage - slot 0 should have 5 diamonds left
        Assertions.assertEquals(ItemResource.of(Items.DIAMOND), sourceStorage.getResource(0));
        Assertions.assertEquals(5, sourceStorage.getAmountAsInt(0));

        // Check destination storage - slot 0 should have 15 diamonds
        Assertions.assertEquals(ItemResource.of(Items.DIAMOND), destStorage.getResource(0));
        Assertions.assertEquals(15, destStorage.getAmountAsInt(0));

        // Check that emeralds were not touched
        Assertions.assertEquals(ItemResource.of(Items.EMERALD), sourceStorage.getResource(1));
        Assertions.assertEquals(30, sourceStorage.getAmountAsInt(1));
    }

    @Test
    public void testMoveInto_WithExistingContents() {
        // Create source storage with multiple fluid tanks
        var sourceTank1 = new SingleResourceSlotKey<FluidResource>();
        var sourceTank2 = new SingleResourceSlotKey<FluidResource>();
        var sourceLayout = FluidStorageLayout.builder()
            .add(sourceTank1, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .add(sourceTank2, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .build();
        FluidStorage sourceStorage = new FluidStorage(sourceLayout);
        sourceStorage.set(0, FluidResource.of(Fluids.WATER), 8000);
        sourceStorage.set(1, FluidResource.of(Fluids.LAVA), 6000);

        // Create destination storage with 2 tanks
        var destTank1 = new SingleResourceSlotKey<FluidResource>();
        var destTank2 = new SingleResourceSlotKey<FluidResource>();
        var destLayout = FluidStorageLayout.builder()
            .add(destTank1, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .add(destTank2, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .build();
        FluidStorage destStorage = new FluidStorage(destLayout);

        // Move only water from source to destination tank 0
        // Lava won't move despite the filter, because water is in the first slot of the source.
        int amountMoved = EnderResourceUtil.moveInto(
            sourceStorage,
            destStorage,
            0,
            _ -> true, // Accept all fluids for now
            5000,
            null
        );

        // Assert correct amount was moved
        Assertions.assertEquals(5000, amountMoved, "Should move 5000 water");

        // Check source - tank 0 should have 3000 water left, tank 1 unchanged
        Assertions.assertEquals(FluidResource.of(Fluids.WATER), sourceStorage.getResource(0));
        Assertions.assertEquals(3000, sourceStorage.getAmountAsInt(0));
        Assertions.assertEquals(FluidResource.of(Fluids.LAVA), sourceStorage.getResource(1));
        Assertions.assertEquals(6000, sourceStorage.getAmountAsInt(1));

        // Check destination - tank 0 should have 5000 water, tank 1 empty
        Assertions.assertEquals(FluidResource.of(Fluids.WATER), destStorage.getResource(0));
        Assertions.assertEquals(5000, destStorage.getAmountAsInt(0));
        Assertions.assertEquals(FluidResource.EMPTY, destStorage.getResource(1));
        Assertions.assertEquals(0, destStorage.getAmountAsInt(1));
    }

    // endregion
}
