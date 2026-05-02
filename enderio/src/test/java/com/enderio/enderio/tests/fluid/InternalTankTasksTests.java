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
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.BiPredicate;

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
    private ItemStorage createItemStorage(BiPredicate<Integer, ItemResource> inputFilter) {
        ItemStorageLayout layout = ItemStorageLayout.builder()
            .add(INPUT_SLOT, SlotTemplates.input(), slot -> slot.capacity(64).filter(inputFilter))
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

    // region fillUsingItem tests

    @Test
    public void fillUsingItem_WaterBucket(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);

        // Due to validation, the restricted ItemAccess should automatically push empties into the output
        ItemStorage itemStorage = createItemStorage(this::isNonEmptyFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToDrain = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int filled = InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, filled);
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT));
        Assertions.assertEquals(Fluids.WATER, fluidStorage.getResource(TANK_SLOT).getFluid());

        // Ensure the item has moved from the input to the output due to slot filtering rules.
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT));
        Assertions.assertEquals(Items.BUCKET, itemStorage.getResource(OUTPUT_SLOT).getItem());
    }

    @Test
    public void fillUsingItem_MultipleWaterBuckets(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);

        // Due to validation, the restricted ItemAccess should automatically push empties into the output
        ItemStorage itemStorage = createItemStorage(this::isNonEmptyFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToDrain = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int filled = InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);
        filled += InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 2, filled);
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 2, fluidStorage.getAmountAsInt(TANK_SLOT));
        Assertions.assertEquals(Fluids.WATER, fluidStorage.getResource(TANK_SLOT).getFluid());

        // Ensure the item has moved from the input to the output due to slot filtering rules.
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(2, itemStorage.getAmountAsInt(OUTPUT_SLOT));
        Assertions.assertEquals(Items.BUCKET, itemStorage.getResource(OUTPUT_SLOT).getItem());
    }

    @Test
    public void fillUsingItem_FluidTank(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);

        // Due to validation, the restricted ItemAccess should automatically push empties into the output
        ItemStorage itemStorage = createItemStorage(this::isNonEmptyFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(getFilledFluidTank(Fluids.WATER, FluidType.BUCKET_VOLUME)), 1);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToDrain = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int filled = InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, filled);
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT));
        Assertions.assertEquals(Fluids.WATER, fluidStorage.getResource(TANK_SLOT).getFluid());

        // Ensure the item has moved from the input to the output due to slot filtering rules.
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT));
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), itemStorage.getResource(OUTPUT_SLOT).getItem());
    }

    @Test
    public void fillUsingItem_FluidTank_PartialFill(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME * 9);

        // Due to validation, the restricted ItemAccess should automatically push empties into the output
        ItemStorage itemStorage = createItemStorage(this::isNonEmptyFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(getFilledFluidTank(Fluids.WATER, FluidType.BUCKET_VOLUME * 2)), 1);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToDrain = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int filled = InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, filled);
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 10, fluidStorage.getAmountAsInt(TANK_SLOT));
        Assertions.assertEquals(Fluids.WATER, fluidStorage.getResource(TANK_SLOT).getFluid());

        // Ensure the fluid tank remains inside the input slot, but with less fluid inside.
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), itemStorage.getResource(INPUT_SLOT).getItem());
        Assertions.assertEquals(EnderResourceUtil.getItemAccess(itemStorage, INPUT_SLOT).getCapability(Capabilities.Fluid.ITEM).getAmountAsInt(0), FluidType.BUCKET_VOLUME);
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void fillUsingItem_FullTank_NothingChanges(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME * 10);

        // Due to validation, the restricted ItemAccess should automatically push empties into the output
        ItemStorage itemStorage = createItemStorage(this::isNonEmptyFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToDrain = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int filled = InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);

        // Assert
        Assertions.assertEquals(0, filled);

        // Ensure the item stayed in the input
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(Items.WATER_BUCKET, itemStorage.getResource(INPUT_SLOT).getItem());
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void fillUsingItem_OutputSlotFull_NothingChanges(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);

        // Due to validation, the restricted ItemAccess should automatically push empties into the output
        ItemStorage itemStorage = createItemStorage(this::isNonEmptyFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.WATER_BUCKET), 1);
        itemStorage.set(OUTPUT_SLOT, ItemResource.of(Items.BUCKET), Items.BUCKET.getDefaultMaxStackSize());

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToDrain = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int filled = InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);

        // Assert
        Assertions.assertEquals(0, filled);

        // Ensure the item stayed in the input
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(Items.WATER_BUCKET, itemStorage.getResource(INPUT_SLOT).getItem());
        Assertions.assertEquals(Items.BUCKET.getDefaultMaxStackSize(), itemStorage.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void fillUsingItem_WrongFluidType_NothingChanges(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        // Due to validation, the restricted ItemAccess should automatically push empties into the output
        ItemStorage itemStorage = createItemStorage(this::isNonEmptyFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.LAVA_BUCKET), 1);
        itemStorage.set(OUTPUT_SLOT, ItemResource.of(Items.BUCKET), Items.BUCKET.getDefaultMaxStackSize());

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToDrain = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int filled = InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);

        // Assert
        Assertions.assertEquals(0, filled);

        // Ensure the item stayed in the input
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(Items.LAVA_BUCKET, itemStorage.getResource(INPUT_SLOT).getItem());
        Assertions.assertEquals(Items.BUCKET.getDefaultMaxStackSize(), itemStorage.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void fillUsingItem_NoInput_NothingChanges(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);

        // Due to validation, the restricted ItemAccess should automatically push empties into the output
        ItemStorage itemStorage = createItemStorage(this::isNonEmptyFluidStorage);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToDrain = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int filled = InternalTankTasks.fillUsingItem(fluidStorage, TANK_SLOT, itemToDrain);

        // Assert
        Assertions.assertEquals(0, filled);

        // Ensure the item stayed in the input
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT));
    }

    private boolean isNonEmptyFluidStorage(int index, ItemResource itemResource) {
        var fluidHandler = ItemAccess.forStack(itemResource.toStack()).getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandler == null) {
            return false;
        }

        return !ResourceHandlerUtil.isEmpty(fluidHandler);
    }

    // endregion
    
    // region drainIntoItem tests

    @Test
    public void drainIntoItem_EmptyBucket(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        ItemStorage itemStorage = createItemStorage(this::isNonFullFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.BUCKET), 1);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToFill = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int drained = InternalTankTasks.drainIntoItem(fluidStorage, TANK_SLOT, itemToFill);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, drained);
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT));

        Assertions.assertEquals(0, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT));
        Assertions.assertEquals(Items.WATER_BUCKET, itemStorage.getResource(OUTPUT_SLOT).getItem());
    }

    @Test
    public void drainIntoItem_MultipleEmptyBuckets_OneBucketFilled(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        ItemStorage itemStorage = createItemStorage(this::isNonFullFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(Items.BUCKET), 2);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToFill = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int drained = InternalTankTasks.drainIntoItem(fluidStorage, TANK_SLOT, itemToFill);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, drained);
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT));

        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(OUTPUT_SLOT));
        Assertions.assertEquals(Items.WATER_BUCKET, itemStorage.getResource(OUTPUT_SLOT).getItem());
    }

    @Test
    public void drainIntoItem_EmptyFluidTank_TankNotFull(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        ItemStorage itemStorage = createItemStorage(this::isNonFullFluidStorage);
        itemStorage.set(INPUT_SLOT, ItemResource.of(EIOBlocks.FLUID_TANK.asItem()), 1);

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToFill = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int drained = InternalTankTasks.drainIntoItem(fluidStorage, TANK_SLOT, itemToFill);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, drained);
        Assertions.assertEquals(0, fluidStorage.getAmountAsInt(TANK_SLOT));

        // n.b. the item should remain in the input slot because it still has room for more fluid.
        // the Fluid Tank should ideally also filter for whether the tank has a *different* fluid in case it's no longer a valid target, and chuck the item to output manually.
        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), itemStorage.getResource(INPUT_SLOT).getItem());
        Assertions.assertEquals(EnderResourceUtil.getItemAccess(itemStorage, INPUT_SLOT).getCapability(Capabilities.Fluid.ITEM).getAmountAsInt(0), FluidType.BUCKET_VOLUME);
        Assertions.assertEquals(0, itemStorage.getAmountAsInt(OUTPUT_SLOT));
    }

    @Test
    public void drainIntoItem_FullFluidTank_NothingChanges(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        ItemStorage itemStorage = createItemStorage(this::isNonFullFluidStorage);
        itemStorage.setStack(INPUT_SLOT, getFilledFluidTank(Fluids.WATER, FluidTankBlockEntity.Standard.CAPACITY));

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToFill = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int drained = InternalTankTasks.drainIntoItem(fluidStorage, TANK_SLOT, itemToFill);

        // Assert
        Assertions.assertEquals(0, drained);
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT));

        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), itemStorage.getResource(INPUT_SLOT).getItem());
    }

    @Test
    public void drainIntoItem_FluidTank_WrongFluid_NothingChanges(MinecraftServer server) {
        // Arrange
        FluidStorage fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        fluidStorage.set(TANK_SLOT, FluidResource.of(Fluids.WATER), FluidType.BUCKET_VOLUME);

        ItemStorage itemStorage = createItemStorage(this::isNonFullFluidStorage);
        itemStorage.setStack(INPUT_SLOT, getFilledFluidTank(Fluids.LAVA, FluidType.BUCKET_VOLUME));

        // Restricted handler, item is in input, but exchange can place items in output if they don't match the filter
        ItemAccess itemToFill = EnderResourceUtil.getItemAccessRestricted(itemStorage, INPUT_SLOT, OUTPUT_SLOT);

        // Act
        int drained = InternalTankTasks.drainIntoItem(fluidStorage, TANK_SLOT, itemToFill);

        // Assert
        Assertions.assertEquals(0, drained);
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, fluidStorage.getAmountAsInt(TANK_SLOT));

        Assertions.assertEquals(1, itemStorage.getAmountAsInt(INPUT_SLOT));
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), itemStorage.getResource(INPUT_SLOT).getItem());
    }

    private boolean isNonFullFluidStorage(int index, ItemResource itemResource) {
        var fluidHandler = ItemAccess.forStack(itemResource.toStack()).getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandler == null) {
            return false;
        }

        return !ResourceHandlerUtil.isFull(fluidHandler);
    }

    // endregion

}
