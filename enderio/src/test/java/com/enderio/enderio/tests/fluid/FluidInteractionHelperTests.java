package com.enderio.enderio.tests.fluid;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderResourceUtil;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.foundation.fluid.FluidInteractionHelper;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@SuppressWarnings("unused")
@ExtendWith(EphemeralTestServerProvider.class)
public class FluidInteractionHelperTests {
    private static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();
    private static final SingleResourceSlotKey<ItemResource> INPUT_SLOT = new SingleResourceSlotKey<>();
    private static final SingleResourceSlotKey<ItemResource> OUTPUT_SLOT = new SingleResourceSlotKey<>();
    private static final int BUCKET_VOLUME = FluidType.BUCKET_VOLUME;

    private FluidStorage createFluidStorage(int capacity) {
        return new FluidStorage(FluidStorageLayout.builder()
            .add(TANK_SLOT, SlotTemplates.storage(capacity))
            .build());
    }

    private ItemStorage createItemStorage(ItemStack stack) {
        var storage = new ItemStorage(ItemStorageLayout.builder()
            .add(INPUT_SLOT, SlotTemplates.storage(64))
            .add(OUTPUT_SLOT, SlotTemplates.storage(64))
            .build());
        storage.setStack(INPUT_SLOT, stack);
        return storage;
    }

    private ItemStack getFilledFluidTank(Fluid fluid, int amount) {
        var stack = new ItemStack(EIOBlocks.FLUID_TANK_ITEM.get());
        stack.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(new FluidStack(fluid, amount)));
        return stack;
    }

    private void assertFluid(FluidStorage storage, Fluid fluid, int amount) {
        Assertions.assertEquals(amount, storage.getAmountAsInt(TANK_SLOT));
        Assertions.assertEquals(fluid, storage.getResource(TANK_SLOT).getFluid());
    }

    private void assertItemFluid(ItemStack stack, Fluid fluid, int amount) {
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), stack.getItem());
        Assertions.assertEquals(1, stack.getCount());
        var content = stack.get(EIODataComponents.ITEM_FLUID_CONTENT);
        Assertions.assertNotNull(content);
        Assertions.assertEquals(fluid, content.getFluid());
        Assertions.assertEquals(amount, content.getAmount());
    }

    // region Inserting fluid into storage

    @Test
    public void insertFluid_WaterBucket(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(10 * BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(Items.WATER_BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertTrue(interacted);
        assertFluid(storage, Fluids.WATER, BUCKET_VOLUME);
        Assertions.assertEquals(Items.BUCKET, items.getStack(INPUT_SLOT).getItem());
        Assertions.assertEquals(1, items.getStack(INPUT_SLOT).getCount());
        Assertions.assertEquals(0, items.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void insertFluid_WaterBucket_ExistingFluid(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(10 * BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(Items.WATER_BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertTrue(interacted);
        assertFluid(storage, Fluids.WATER, 2 * BUCKET_VOLUME);
        Assertions.assertEquals(Items.BUCKET, items.getStack(INPUT_SLOT).getItem());
    }

    @Test
    public void insertFluid_FluidTank_PartialTransfer(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(10 * BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), 9 * BUCKET_VOLUME);
        var items = createItemStorage(getFilledFluidTank(Fluids.WATER, 2 * BUCKET_VOLUME));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertTrue(interacted);
        assertFluid(storage, Fluids.WATER, 10 * BUCKET_VOLUME);
        assertItemFluid(items.getStack(INPUT_SLOT), Fluids.WATER, BUCKET_VOLUME);
    }

    @Test
    public void insertFluid_FullStorage_NothingChanges(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(Items.WATER_BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertFalse(interacted);
        assertFluid(storage, Fluids.WATER, BUCKET_VOLUME);
        Assertions.assertEquals(Items.WATER_BUCKET, items.getStack(INPUT_SLOT).getItem());
        Assertions.assertEquals(1, items.getStack(INPUT_SLOT).getCount());
        Assertions.assertEquals(0, items.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void insertFluid_BucketWithInsufficientSpace_RollsBack(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(BUCKET_VOLUME / 2);
        var items = createItemStorage(new ItemStack(Items.WATER_BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertFalse(interacted);
        Assertions.assertEquals(0, storage.getAmountAsInt(TANK_SLOT));
        Assertions.assertTrue(storage.getResource(TANK_SLOT).isEmpty());
        Assertions.assertEquals(Items.WATER_BUCKET, items.getStack(INPUT_SLOT).getItem());
        Assertions.assertEquals(0, items.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void insertFluid_OutputOnlyStorage_NothingChanges(MinecraftServer server) {
        // Arrange
        var storage = new FluidStorage(FluidStorageLayout.builder()
            .add(TANK_SLOT, SlotTemplates.output(BUCKET_VOLUME))
            .build());
        var items = createItemStorage(new ItemStack(Items.WATER_BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertFalse(interacted);
        Assertions.assertEquals(0, storage.getAmountAsInt(TANK_SLOT));
        Assertions.assertEquals(Items.WATER_BUCKET, items.getStack(INPUT_SLOT).getItem());
        Assertions.assertEquals(0, items.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void insertFluid_WrongFluid_NothingChanges(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(10 * BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(Items.LAVA_BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertAll(
            () -> Assertions.assertFalse(interacted),
            () -> assertFluid(storage, Fluids.WATER, BUCKET_VOLUME),
            () -> Assertions.assertEquals(Items.LAVA_BUCKET, items.getStack(INPUT_SLOT).getItem()),
            () -> Assertions.assertEquals(1, items.getStack(INPUT_SLOT).getCount()),
            () -> Assertions.assertEquals(0, items.getAmountAsInt(OUTPUT_SLOT))
        );
    }

    // endregion

    // region Taking fluid from storage

    @Test
    public void takeFluid_EmptyBucket(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(10 * BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), 2 * BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(Items.BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertTrue(interacted);
        assertFluid(storage, Fluids.WATER, BUCKET_VOLUME);
        Assertions.assertEquals(Items.WATER_BUCKET, items.getStack(INPUT_SLOT).getItem());
        Assertions.assertEquals(1, items.getStack(INPUT_SLOT).getCount());
        Assertions.assertEquals(0, items.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void takeFluid_StackedBuckets_FillsOneBucket(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(10 * BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), 2 * BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(Items.BUCKET, 2));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertAll(
            () -> Assertions.assertTrue(interacted),
            () -> assertFluid(storage, Fluids.WATER, BUCKET_VOLUME),
            () -> Assertions.assertEquals(Items.BUCKET, items.getResource(INPUT_SLOT).getItem()),
            () -> Assertions.assertEquals(Items.WATER_BUCKET, items.getResource(OUTPUT_SLOT).getItem()),
            () -> Assertions.assertEquals(1, items.getAmountAsInt(INPUT_SLOT)),
            () -> Assertions.assertEquals(1, items.getAmountAsInt(OUTPUT_SLOT))
        );
    }

    @Test
    public void takeFluid_EmptyFluidTank_PartialTransfer(MinecraftServer server) {
        // Arrange
        int capacity = FluidTankBlockEntity.Standard.CAPACITY;
        var storage = createFluidStorage(capacity + BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), capacity + BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(EIOBlocks.FLUID_TANK_ITEM.get()));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertTrue(interacted);
        assertFluid(storage, Fluids.WATER, BUCKET_VOLUME);
        assertItemFluid(items.getStack(INPUT_SLOT), Fluids.WATER, capacity);
    }

    @Test
    public void takeFluid_EmptyStorage_NothingChanges(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(Items.BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertFalse(interacted);
        Assertions.assertEquals(0, storage.getAmountAsInt(TANK_SLOT));
        Assertions.assertTrue(storage.getResource(TANK_SLOT).isEmpty());
        Assertions.assertEquals(Items.BUCKET, items.getStack(INPUT_SLOT).getItem());
        Assertions.assertEquals(1, items.getStack(INPUT_SLOT).getCount());
        Assertions.assertEquals(0, items.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void takeFluid_InsufficientFluidForBucket_NothingChanges(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), BUCKET_VOLUME / 2);
        var items = createItemStorage(new ItemStack(Items.BUCKET));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertAll(
            () -> Assertions.assertFalse(interacted),
            () -> assertFluid(storage, Fluids.WATER, BUCKET_VOLUME / 2),
            () -> Assertions.assertEquals(Items.BUCKET, items.getStack(INPUT_SLOT).getItem()),
            () -> Assertions.assertEquals(1, items.getStack(INPUT_SLOT).getCount()),
            () -> Assertions.assertEquals(0, items.getAmountAsInt(OUTPUT_SLOT))
        );
    }

    // endregion

    @Test
    public void interact_ItemWithoutFluidCapability_NothingChanges(MinecraftServer server) {
        // Arrange
        var storage = createFluidStorage(BUCKET_VOLUME);
        storage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), BUCKET_VOLUME);
        var items = createItemStorage(new ItemStack(Items.STICK));

        // Act
        boolean interacted = FluidInteractionHelper.handleFluidItemInteraction(EnderResourceUtil.getItemAccess(items, INPUT_SLOT), storage, TANK_SLOT);

        // Assert
        Assertions.assertFalse(interacted);
        assertFluid(storage, Fluids.WATER, BUCKET_VOLUME);
        Assertions.assertEquals(Items.STICK, items.getStack(INPUT_SLOT).getItem());
        Assertions.assertEquals(1, items.getStack(INPUT_SLOT).getCount());
    }
}
