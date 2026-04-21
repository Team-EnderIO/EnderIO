package com.enderio.enderio.tests.fluid;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderResourceUtil;
import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.content.storage.fluid_tank.InternalTankTasks;
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

@ExtendWith(EphemeralTestServerProvider.class)
public class InternalTankTasksTests {

    // Define slot keys for testing
    private static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();
    private static final SingleResourceSlotKey<ItemResource> INPUT_SLOT = new SingleResourceSlotKey<>();
    private static final SingleResourceSlotKey<ItemResource> OUTPUT_SLOT = new SingleResourceSlotKey<>();

    // Helper method to create a test fluid storage
    private FluidStorage createFluidStorage(int capacity) {
        FluidStorageLayout layout = FluidStorageLayout.builder()
            .add(TANK_SLOT, SlotTemplates.storage(), slot -> slot.capacity(capacity))
            .build();
        return new FluidStorage(layout);
    }

    // Helper method to create a test item storage
    private ItemStorage createItemStorage() {
        ItemStorageLayout layout = ItemStorageLayout.builder()
            .add(INPUT_SLOT, SlotTemplates.input(), slot -> slot.capacity(64))
            .add(OUTPUT_SLOT, SlotTemplates.output(), slot -> slot.capacity(64))
            .build();
        return new ItemStorage(layout);
    }

    // Helper method to get a fluid tank item filled with a fluid
    private ItemStack getFilledFluidTank(Fluid fluid, int amount) {
        var stack = new ItemStack(EIOBlocks.FLUID_TANK_ITEM.get(), 1);
        stack.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(new FluidStack(fluid, amount)));
        return stack;
    }

    // region fillInternal tests

    @Test
    public void testFillInternalWithWaterBucket(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Set up input: water bucket
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should contain one bucket of water");
        Assertions.assertEquals(Fluids.WATER, fluidStorage.getResource(TANK_SLOT).getFluid(),
            "Fluid tank should contain water");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input slot should be empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output slot should contain one empty bucket");
        Assertions.assertEquals(Items.BUCKET, itemStorage.getResource(OUTPUT_SLOT).getItem(),
            "Output slot should contain an empty bucket");
    }

    @Test
    public void testFillInternalWithMultipleBuckets(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Set up input
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);

        // Act - first fill
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert after first fill
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT));
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input should have no bucket remaining");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT));

        // Set up input again
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);

        // Act - second fill
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert after second fill
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 2, fluidStorage.getAmountAsInt(TANK_SLOT));
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input should have no bucket remaining");
        Assertions.assertEquals(2, itemStorage.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void testFillInternalWithFullTank(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME);
        ItemStorage itemStorage = createItemStorage();

        // Fill tank completely
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Try to add another bucket
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - nothing should change
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should remain full");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input bucket should remain");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output should be empty");
    }

    @Test
    public void testFillInternalWithOutputSlotFull(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Set up input: water bucket
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);
        // Fill output slot completely
        itemStorage.set(OUTPUT_SLOT, ItemResource.of(Items.BUCKET), 64);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - nothing should change because output is full
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should remain empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input bucket should remain");
        Assertions.assertEquals(64, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output should remain full");
    }

    @Test
    public void testFillInternalWithEmptyInput(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Input is empty
        itemStorage.set(INPUT_SLOT, ItemResource.of(ItemStack.EMPTY), 0);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - nothing should happen
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should remain empty");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output should remain empty");
    }

    @Test
    public void testFillInternalWithInvalidInput(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Set up input: cobblestone (not a bucket)
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.COBBLESTONE), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - nothing should happen
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should remain empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input item should remain");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output should remain empty");
    }

    @Test
    public void testFillInternalWithDifferentFluidType(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Tank already has water
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Try to add lava bucket
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.LAVA_BUCKET), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - should not mix fluids
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should still contain one bucket");
        Assertions.assertEquals(Fluids.WATER, fluidStorage.getResource(TANK_SLOT).getFluid(),
            "Fluid tank should still contain water");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Lava bucket should remain in input");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output should remain empty");
    }

    @Test
    public void testFillInternalWithOutputSlotBlocked(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Set up input: water bucket
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);
        // Output slot has glass bottle
        itemStorage.set(OUTPUT_SLOT, ItemResource.of(Items.GLASS_BOTTLE), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - should not work because output has wrong item
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should remain empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input bucket should remain");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output should still contain a glass bottle");
    }

    // ========== Tests for fluid handler capability path (non-bucket items) ==========

    // Fluid container tests are commented out because they require the full NeoForge capability system
    // which is not available in unit tests. These should be converted to game tests.

    @Test
    public void testFillInternalWithFluidContainer(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Create a fluid container with 500mb of water (half a bucket)
        ItemStack fluidContainer = getFilledFluidTank(Fluids.WATER, 500);
        itemStorage.set(INPUT_SLOT, ItemResource.of(fluidContainer), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(500, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should contain 500mb of water");
        Assertions.assertEquals(Fluids.WATER, fluidStorage.getResource(TANK_SLOT).getFluid(),
            "Fluid tank should contain water");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input slot should be empty after transfer");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output slot should contain the empty container");
    }

    @Test
    public void testFillInternalWithFluidContainerToPartiallyFilledTank(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Pre-fill tank with 1 bucket
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Create a fluid container with 2 buckets of water
        ItemStack fluidContainer = getFilledFluidTank(Fluids.WATER, FluidType.BUCKET_VOLUME * 2);
        itemStorage.set(INPUT_SLOT, ItemResource.of(fluidContainer), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 3, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should contain 3 buckets total");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input slot should be empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output slot should contain the empty container");
    }

    @Test
    public void testFillInternalWithFluidContainerExceedingCapacity(MinecraftServer server) {
        // Arrange - tank can only hold 2 buckets
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 2);
        ItemStorage itemStorage = createItemStorage();

        // Create a fluid container with 3 buckets of water (more than tank capacity)
        ItemStack fluidContainer = getFilledFluidTank(Fluids.WATER, FluidType.BUCKET_VOLUME * 3);
        itemStorage.set(INPUT_SLOT, ItemResource.of(fluidContainer), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - only 2 buckets should transfer
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 2, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should be full (2 buckets)");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input slot should be empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output slot should contain the container");

        // Check that the output container has the remaining fluid (1 bucket)
        ItemStack outputStack = EnderResourceUtil.getItemStack(itemStorage, OUTPUT_SLOT);
        var fluidContent = outputStack.get(EIODataComponents.ITEM_FLUID_CONTENT);
        Assertions.assertNotNull(fluidContent, "Output container should have fluid content");
        FluidStack fluidStack = fluidContent.copy();
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStack.getAmount(),
            "Output container should have 1 bucket remaining");
    }

    @Test
    public void testFillInternalWithEmptyFluidContainer(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Create an empty fluid container
        ItemStack emptyContainer = getFilledFluidTank(Fluids.WATER, 0);
        itemStorage.set(INPUT_SLOT, ItemResource.of(emptyContainer), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - nothing should happen with empty container
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should remain empty");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input should be moved even though empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output should contain the empty container");
    }

    @Test
    public void testFillInternalWithFluidContainerWhenOutputNotEmpty(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Create a fluid container with water
        ItemStack fluidContainer = getFilledFluidTank(Fluids.WATER, FluidType.BUCKET_VOLUME);
        itemStorage.set(INPUT_SLOT, ItemResource.of(fluidContainer), 1);

        // Output slot already has something
        itemStorage.set(OUTPUT_SLOT, ItemResource.of(Items.COBBLESTONE), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - transfer should not happen because output is not empty
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should remain empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input container should remain");
        Assertions.assertEquals(Items.COBBLESTONE, itemStorage.getResource(OUTPUT_SLOT).getItem(),
            "Output should still have cobblestone");
    }

    @Test
    public void testFillInternalWithFluidContainerDifferentFluidTypes(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Pre-fill tank with water
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Try to add lava from a fluid container
        ItemStack lavaContainer = getFilledFluidTank(Fluids.LAVA, FluidType.BUCKET_VOLUME);
        itemStorage.set(INPUT_SLOT, ItemResource.of(lavaContainer), 1);

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert - should not mix fluids
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should still contain only 1 bucket");
        Assertions.assertEquals(Fluids.WATER, fluidStorage.getResource(TANK_SLOT).getFluid(),
            "Fluid tank should still contain water");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input should remain in place");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "No output should be generated");

        // The lava should remain in the input slot
        ItemStack outputStack = EnderResourceUtil.getItemStack(itemStorage, INPUT_SLOT);
        var fluidContent = outputStack.get(EIODataComponents.ITEM_FLUID_CONTENT);
        Assertions.assertNotNull(fluidContent, "Input container should have fluid content");
        FluidStack fluidStack = fluidContent.copy();
        Assertions.assertEquals(Fluids.LAVA, fluidStack.getFluid(), "Container should still have lava");
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStack.getAmount(),
            "Container should still have full bucket of lava");
    }

    // endregion
    
    // region drainInternal tests

    @Test
    public void testDrainInternalWithEmptyBucket(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Tank has water
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Set up input: empty bucket
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.BUCKET), 1);

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should be empty");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input slot should be empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output slot should contain one water bucket");
        Assertions.assertEquals(Items.WATER_BUCKET, itemStorage.getResource(OUTPUT_SLOT).getItem(),
            "Output slot should contain one water bucket");
    }

    @Test
    public void testDrainInternalWithWaterBucketItem(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Tank has water
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Set up input: already full water bucket
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should still have water");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input slot should be empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output slot should contain the water bucket");
    }

    @Test
    public void testDrainInternalWithEmptyFluidTank(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Tank has water
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Set up input: empty fluid tank
        itemStorage.setStack(INPUT_SLOT, getFilledFluidTank(Fluids.WATER, 0));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should be empty");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input slot should be empty");
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT),
            "Output slot should contain a fluid tank");
        Assertions.assertEquals(EIOBlocks.FLUID_TANK_ITEM.get(), itemStorage.getResource(OUTPUT_SLOT).getItem(),
            "Output slot should contain a fluid tank");

        // The output container should contain water
        ItemStack outputStack = EnderResourceUtil.getItemStack(itemStorage, OUTPUT_SLOT);
        var fluidContent = outputStack.get(EIODataComponents.ITEM_FLUID_CONTENT);
        Assertions.assertNotNull(fluidContent, "Output container should have fluid content");
        FluidStack fluidStack = fluidContent.copy();
        Assertions.assertEquals(Fluids.WATER, fluidStack.getFluid(), "Container should have water");
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStack.getAmount(),
            "Container should have a buckets worth of water");
    }

    @Test
    public void testDrainInternalWithFullFluidTank(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        ItemStorage itemStorage = createItemStorage();

        // Tank has water
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Set up input: empty fluid tank
        itemStorage.setStack(INPUT_SLOT, getFilledFluidTank(Fluids.WATER, FluidTankBlockEntity.Standard.CAPACITY));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT),
            "Fluid tank should still have water");
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT),
            "Input slot should be empty");
        Assertions.assertEquals(EIOBlocks.FLUID_TANK_ITEM.get(), itemStorage.getResource(OUTPUT_SLOT).getItem(),
            "Output slot should contain a fluid tank");
    }

    // endregion

}
