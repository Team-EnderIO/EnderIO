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

    // region tryMoveResource tests

    @Test
    public void testTryMoveItem_Success() {
        // Create layout with 2 storage slots
        var slot1 = new SingleResourceSlotKey<ItemResource>();
        var slot2 = new SingleResourceSlotKey<ItemResource>();
        var layout = ItemStorageLayout.<Void>builder()
            .slot(slot1, SlotTemplates.storage())
            .slot(slot2, SlotTemplates.storage())
            .build();

        // Create storage with an item in slot 0 and empty slot 1
        ItemStorage<Void> storage = new ItemStorage<>(layout, null);
        storage.set(0, ItemResource.of(Items.DIAMOND), 10);

        // Move item from slot 0 to slot 1
        boolean result = EnderResourceUtil.tryMoveItem(storage, 0, 1, null);

        // Assert move was successful
        Assertions.assertTrue(result, "Move should succeed");
        Assertions.assertEquals(ItemResource.EMPTY, storage.getResource(0), "Source slot should be empty");
        Assertions.assertEquals(0, storage.getAmountAsInt(0), "Source slot should have 0 items");
        Assertions.assertEquals(ItemResource.of(Items.DIAMOND), storage.getResource(1), "Destination slot should have diamonds");
        Assertions.assertEquals(10, storage.getAmountAsInt(1), "Destination slot should have 10 items");
    }

    @Test
    public void testTryMoveItem_FailsWhenDestinationFull() {
        // Create layout with 2 storage slots
        var slot1 = new SingleResourceSlotKey<ItemResource>();
        var slot2 = new SingleResourceSlotKey<ItemResource>();
        var layout = ItemStorageLayout.<Void>builder()
            .slot(slot1, SlotTemplates.storage())
            .slot(slot2, SlotTemplates.storage())
            .build();

        // Create storage with items in both slots
        ItemStorage<Void> storage = new ItemStorage<>(layout, null);
        storage.set(0, ItemResource.of(Items.DIAMOND), 32);
        storage.set(1, ItemResource.of(Items.EMERALD), 64); // Full slot with different item

        // Try to move item from slot 0 to slot 1
        boolean result = EnderResourceUtil.tryMoveItem(storage, 0, 1, null);

        // Assert move failed
        Assertions.assertFalse(result, "Move should fail when destination is full with different item");
        Assertions.assertEquals(ItemResource.of(Items.DIAMOND), storage.getResource(0), "Source slot should still have diamonds");
        Assertions.assertEquals(32, storage.getAmountAsInt(0), "Source slot should still have 32 items");
        Assertions.assertEquals(ItemResource.of(Items.EMERALD), storage.getResource(1), "Destination slot should still have emeralds");
        Assertions.assertEquals(64, storage.getAmountAsInt(1), "Destination slot should still have 64 items");
    }

    @Test
    public void testTryMoveFluid_Success() {
        // Create layout with 2 fluid tanks, each with 10000 capacity
        var tank1 = new SingleResourceSlotKey<FluidResource>();
        var tank2 = new SingleResourceSlotKey<FluidResource>();
        var layout = FluidStorageLayout.<Void>builder()
            .slot(tank1, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .slot(tank2, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .build();

        // Create storage with water in tank 0 and empty tank 1
        FluidStorage<Void> storage = new FluidStorage<>(layout, null);
        storage.set(0, FluidResource.of(Fluids.WATER), 5000);

        // Move fluid from tank 0 to tank 1
        boolean result = EnderResourceUtil.tryMoveFluid(storage, 0, 1, null);

        // Assert move was successful
        Assertions.assertTrue(result, "Move should succeed");
        Assertions.assertEquals(FluidResource.EMPTY, storage.getResource(0), "Source tank should be empty");
        Assertions.assertEquals(0, storage.getAmountAsInt(0), "Source tank should have 0 fluid");
        Assertions.assertEquals(FluidResource.of(Fluids.WATER), storage.getResource(1), "Destination tank should have water");
        Assertions.assertEquals(5000, storage.getAmountAsInt(1), "Destination tank should have 5000 fluid");
    }

    @Test
    public void testTryMoveFluid_FailsWhenDestinationFull() {
        // Create layout with 2 fluid tanks
        var tank1 = new SingleResourceSlotKey<FluidResource>();
        var tank2 = new SingleResourceSlotKey<FluidResource>();
        var layout = FluidStorageLayout.<Void>builder()
            .slot(tank1, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .slot(tank2, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .build();

        // Create storage with water in tank 0 and lava in tank 1 (full)
        FluidStorage<Void> storage = new FluidStorage<>(layout, null);
        storage.set(0, FluidResource.of(Fluids.WATER), 5000);
        storage.set(1, FluidResource.of(Fluids.LAVA), 10000); // Full tank with different fluid

        // Try to move fluid from tank 0 to tank 1
        boolean result = EnderResourceUtil.tryMoveFluid(storage, 0, 1, null);

        // Assert move failed
        Assertions.assertFalse(result, "Move should fail when destination is full with different fluid");
        Assertions.assertEquals(FluidResource.of(Fluids.WATER), storage.getResource(0), "Source tank should still have water");
        Assertions.assertEquals(5000, storage.getAmountAsInt(0), "Source tank should still have 5000 fluid");
        Assertions.assertEquals(FluidResource.of(Fluids.LAVA), storage.getResource(1), "Destination tank should still have lava");
        Assertions.assertEquals(10000, storage.getAmountAsInt(1), "Destination tank should still have 10000 fluid");
    }

    // endregion

    // region moveInto tests

    @Test
    public void testMoveInto_Success() {
        // Create source storage with 2 slots containing items
        var sourceSlot1 = new SingleResourceSlotKey<ItemResource>();
        var sourceSlot2 = new SingleResourceSlotKey<ItemResource>();
        var sourceLayout = ItemStorageLayout.<Void>builder()
            .slot(sourceSlot1, SlotTemplates.storage())
            .slot(sourceSlot2, SlotTemplates.storage())
            .build();
        ItemStorage<Void> sourceStorage = new ItemStorage<>(sourceLayout, null);
        sourceStorage.set(0, ItemResource.of(Items.DIAMOND), 20);
        sourceStorage.set(1, ItemResource.of(Items.EMERALD), 30);

        // Create destination storage with 2 empty slots
        var destSlot1 = new SingleResourceSlotKey<ItemResource>();
        var destSlot2 = new SingleResourceSlotKey<ItemResource>();
        var destLayout = ItemStorageLayout.<Void>builder()
            .slot(destSlot1, SlotTemplates.storage())
            .slot(destSlot2, SlotTemplates.storage())
            .build();
        ItemStorage<Void> destStorage = new ItemStorage<>(destLayout, null);

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
        var sourceLayout = FluidStorageLayout.<Void>builder()
            .slot(sourceTank1, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .slot(sourceTank2, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .build();
        FluidStorage<Void> sourceStorage = new FluidStorage<>(sourceLayout, null);
        sourceStorage.set(0, FluidResource.of(Fluids.WATER), 8000);
        sourceStorage.set(1, FluidResource.of(Fluids.LAVA), 6000);

        // Create destination storage with 2 tanks
        var destTank1 = new SingleResourceSlotKey<FluidResource>();
        var destTank2 = new SingleResourceSlotKey<FluidResource>();
        var destLayout = FluidStorageLayout.<Void>builder()
            .slot(destTank1, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .slot(destTank2, SlotTemplates.storage(), slot -> slot.capacity(10000))
            .build();
        FluidStorage<Void> destStorage = new FluidStorage<>(destLayout, null);

        // Move only water from source to destination tank 0
        // Lava won't move despite the filter, because water is in the first slot of the source.
        int amountMoved = EnderResourceUtil.moveInto(
            sourceStorage,
            destStorage,
            0,
            resource -> true, // Accept all fluids for now
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
