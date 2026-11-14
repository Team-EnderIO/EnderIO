package com.enderio.enderio.tests.fluid;

import com.enderio.enderio.content.storage.fluid_tank.FluidTankBlockEntity;
import com.enderio.enderio.content.storage.fluid_tank.InternalTankTasks;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.DumbIOConfigurable;
import com.enderio.enderio.foundation.io.fluid.MachineFluidHandler;
import com.enderio.enderio.foundation.io.fluid.MachineTankLayout;
import com.enderio.enderio.foundation.io.fluid.TankAccess;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIOBlocks;
import com.enderio.enderio.init.EIOFluids;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class InternalTankTasksTests {

    // Define slot keys for testing
    private static final TankAccess TANK_SLOT = new TankAccess();
    private static final SingleSlotAccess FILL_INPUT_SLOT = new SingleSlotAccess();
    private static final SingleSlotAccess FILL_OUTPUT_SLOT = new SingleSlotAccess();
    private static final SingleSlotAccess DRAIN_INPUT_SLOT = new SingleSlotAccess();
    private static final SingleSlotAccess DRAIN_OUTPUT_SLOT = new SingleSlotAccess();

    // Helper method to create a test fluid storage
    private MachineFluidHandler createFluidStorage(int capacity) {
        MachineTankLayout layout = MachineTankLayout.builder()
            .tank(TANK_SLOT, capacity)
            .build();
        return new MachineFluidHandler(DumbIOConfigurable.DISABLED, layout);
    }

    // Helper method to create a test item storage
    private MachineInventory createItemStorage() {
        MachineInventoryLayout layout = MachineInventoryLayout.builder()
            .inputSlot((index, stack) -> isNonEmptyFluidStorage(stack))
            .slotAccess(FILL_INPUT_SLOT)
            .outputSlot()
            .slotAccess(FILL_OUTPUT_SLOT)
            .inputSlot((index, stack) -> isNonFullFluidStorage(stack))
            .slotAccess(DRAIN_INPUT_SLOT)
            .outputSlot()
            .slotAccess(DRAIN_OUTPUT_SLOT)
            .build();
        return new MachineInventory(DumbIOConfigurable.DISABLED, layout);
    }

    // Helper method to get a fluid tank item filled with a fluid
    private ItemStack getFilledFluidTank(Fluid fluid, int amount) {
        var stack = new ItemStack(EIOBlocks.FLUID_TANK.asItem(), 1);
        if (amount > 0) {
            var fluidHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
            fluidHandler.fill(new FluidStack(fluid, amount), IFluidHandler.FluidAction.EXECUTE);
            return fluidHandler.getContainer();
        }
        return stack;
    }

    // Helper method to get the amount of fluid stored inside an item
    private int getFluidAmount(ItemStack stack) {
        var fluidHandler = stack.copyWithCount(1).getCapability(Capabilities.FluidHandler.ITEM);
        return fluidHandler.getFluidInTank(0).getAmount();
    }

    // region fillInternal tests

    @Test
    public void fillInternal_WaterBucket(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        MachineInventory itemStorage = createItemStorage();
        FILL_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.WATER_BUCKET));

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, TANK_SLOT.getFluidAmount(fluidStorage));
        Assertions.assertEquals(Fluids.WATER, TANK_SLOT.getFluid(fluidStorage).getFluid());

        // Ensure the item has moved from the input to the output, and is now empty.
        Assertions.assertTrue(FILL_INPUT_SLOT.getItemStack(itemStorage).isEmpty());
        Assertions.assertEquals(1, FILL_OUTPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.BUCKET, FILL_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
    }

    @Test
    public void fillInternal_MultipleWaterBuckets(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        MachineInventory itemStorage = createItemStorage();
        FILL_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.WATER_BUCKET));

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);
        FILL_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.WATER_BUCKET));
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 2, TANK_SLOT.getFluidAmount(fluidStorage));
        Assertions.assertEquals(Fluids.WATER, TANK_SLOT.getFluid(fluidStorage).getFluid());

        // Ensure the items have moved from the input to the output, and are now empty.
        Assertions.assertTrue(FILL_INPUT_SLOT.getItemStack(itemStorage).isEmpty());
        Assertions.assertEquals(2, FILL_OUTPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.BUCKET, FILL_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
    }

    @Test
    public void fillInternal_FluidTank(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        MachineInventory itemStorage = createItemStorage();
        FILL_INPUT_SLOT.setStackInSlot(itemStorage, getFilledFluidTank(Fluids.WATER, FluidType.BUCKET_VOLUME));

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, TANK_SLOT.getFluidAmount(fluidStorage));
        Assertions.assertEquals(Fluids.WATER, TANK_SLOT.getFluid(fluidStorage).getFluid());

        // Ensure the item has moved from the input to the output, and is now empty.
        Assertions.assertTrue(FILL_INPUT_SLOT.getItemStack(itemStorage).isEmpty());
        Assertions.assertEquals(1, FILL_OUTPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), FILL_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(0, getFluidAmount(FILL_OUTPUT_SLOT.getItemStack(itemStorage)));
    }

    @Test
    public void fillInternal_FluidTank_PartialFill(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME * 9));

        MachineInventory itemStorage = createItemStorage();
        FILL_INPUT_SLOT.setStackInSlot(itemStorage, getFilledFluidTank(Fluids.WATER, FluidType.BUCKET_VOLUME * 2));

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 10, TANK_SLOT.getFluidAmount(fluidStorage));
        Assertions.assertEquals(Fluids.WATER, TANK_SLOT.getFluid(fluidStorage).getFluid());

        // Ensure the fluid tank remains inside the input slot, but with less fluid inside.
        Assertions.assertEquals(1, FILL_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), FILL_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, getFluidAmount(FILL_INPUT_SLOT.getItemStack(itemStorage)));
        Assertions.assertTrue(FILL_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void fillInternal_FullTank_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME * 10));

        MachineInventory itemStorage = createItemStorage();
        FILL_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.WATER_BUCKET));

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 10, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the item stayed in the input
        Assertions.assertEquals(1, FILL_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.WATER_BUCKET, FILL_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertTrue(FILL_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void fillInternal_OutputSlotFull_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);

        MachineInventory itemStorage = createItemStorage();
        FILL_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.WATER_BUCKET));
        FILL_OUTPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.BUCKET, Items.BUCKET.getDefaultMaxStackSize()));

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the item stayed in the input
        Assertions.assertEquals(1, FILL_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.WATER_BUCKET, FILL_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(Items.BUCKET.getDefaultMaxStackSize(), FILL_OUTPUT_SLOT.getItemStack(itemStorage).getCount());
    }

    @Test
    public void fillInternal_WrongFluidType_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));

        MachineInventory itemStorage = createItemStorage();
        FILL_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.LAVA_BUCKET));

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, TANK_SLOT.getFluidAmount(fluidStorage));
        Assertions.assertEquals(Fluids.WATER, TANK_SLOT.getFluid(fluidStorage).getFluid());

        // Ensure the item stayed in the input
        Assertions.assertEquals(1, FILL_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.LAVA_BUCKET, FILL_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertTrue(FILL_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void fillInternal_NoInput_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        MachineInventory itemStorage = createItemStorage();

        // Act
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, itemStorage, FILL_INPUT_SLOT, FILL_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, TANK_SLOT.getFluidAmount(fluidStorage));
        Assertions.assertTrue(FILL_INPUT_SLOT.getItemStack(itemStorage).isEmpty());
        Assertions.assertTrue(FILL_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    private boolean isNonEmptyFluidStorage(ItemStack stack) {
        var fluidHandler = stack.copyWithCount(1).getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler == null) {
            return false;
        }

        return !fluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE).isEmpty();
    }

    // endregion

    // region drainInternal tests

    @Test
    public void drainInternal_EmptyBucket(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.BUCKET));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, TANK_SLOT.getFluidAmount(fluidStorage));

        Assertions.assertTrue(DRAIN_INPUT_SLOT.getItemStack(itemStorage).isEmpty());
        Assertions.assertEquals(1, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.WATER_BUCKET, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
    }

    @Test
    public void drainInternal_MultipleEmptyBuckets_OneBucketFilled(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.BUCKET, 2));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, TANK_SLOT.getFluidAmount(fluidStorage));

        Assertions.assertEquals(1, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(1, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.WATER_BUCKET, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
    }

    @Test
    public void drainInternal_EmptyFluidTank_TankNotFull(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, getFilledFluidTank(Fluids.WATER, 0));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, TANK_SLOT.getFluidAmount(fluidStorage));

        // n.b. the item should remain in the input slot because it still has room for more fluid.
        Assertions.assertEquals(1, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, getFluidAmount(DRAIN_INPUT_SLOT.getItemStack(itemStorage)));
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void drainInternal_FluidTankFilledCompletely_MovesToOutput(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME * 10));

        // Leave room for exactly one bucket in the item.
        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage,
            getFilledFluidTank(Fluids.WATER, FluidTankBlockEntity.Standard.CAPACITY - FluidType.BUCKET_VOLUME));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 9, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the full item has moved from the input to the output
        Assertions.assertTrue(DRAIN_INPUT_SLOT.getItemStack(itemStorage).isEmpty());
        Assertions.assertEquals(1, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(FluidTankBlockEntity.Standard.CAPACITY, getFluidAmount(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage)));
    }

    @Test
    public void drainInternal_EmptyTank_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.BUCKET));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, TANK_SLOT.getFluidAmount(fluidStorage));

        Assertions.assertEquals(1, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.BUCKET, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void drainInternal_NotEnoughFluidForBucket_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME / 2));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.BUCKET));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME / 2, TANK_SLOT.getFluidAmount(fluidStorage));

        Assertions.assertEquals(1, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.BUCKET, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void drainInternal_FluidTank_WrongFluid_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, getFilledFluidTank(Fluids.LAVA, FluidType.BUCKET_VOLUME));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, TANK_SLOT.getFluidAmount(fluidStorage));

        Assertions.assertEquals(1, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(EIOBlocks.FLUID_TANK.asItem(), DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, getFluidAmount(DRAIN_INPUT_SLOT.getItemStack(itemStorage)));
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void drainInternal_OutputBlocked_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.BUCKET));
        DRAIN_OUTPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.STICK));

        // Act
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME, TANK_SLOT.getFluidAmount(fluidStorage));

        Assertions.assertEquals(1, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getCount());
        Assertions.assertEquals(Items.BUCKET, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(Items.STICK, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
    }

    private boolean isNonFullFluidStorage(ItemStack stack) {
        var fluidHandler = stack.copyWithCount(1).getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler == null) {
            return false;
        }

        return fluidHandler.getFluidInTank(0).getAmount() < fluidHandler.getTankCapacity(0);
    }

    // endregion

    // region tryMendTool tests

    @Test
    public void tryMendTool_RepairsTool(MinecraftServer server) {
        // Arrange
        Fluid xp = EIOFluids.XP_JUICE.source().get();
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(xp, FluidType.BUCKET_VOLUME * 10));

        ItemStack tool = getDamagedPickaxe(server, 100, true);
        int xpCost = (int) Math.floor(100 / tool.getXpRepairRatio());

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, tool);

        // Act
        InternalTankTasks.tryMendTool(server.registryAccess(), fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 10 - xpCost * ExperienceUtil.EXP_TO_FLUID, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the repaired tool has moved from the input to the output
        Assertions.assertTrue(DRAIN_INPUT_SLOT.getItemStack(itemStorage).isEmpty());
        Assertions.assertEquals(Items.DIAMOND_PICKAXE, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(0, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getDamageValue());
    }

    @Test
    public void tryMendTool_PartialRepair_WhenTankRunsOut(MinecraftServer server) {
        // Arrange
        Fluid xp = EIOFluids.XP_JUICE.source().get();
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);

        ItemStack tool = getDamagedPickaxe(server, 100, true);
        int xpCost = (int) Math.floor(100 / tool.getXpRepairRatio());
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(xp, xpCost * ExperienceUtil.EXP_TO_FLUID / 2));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, tool);

        // Act
        InternalTankTasks.tryMendTool(server.registryAccess(), fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(0, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the tool was only partially repaired
        Assertions.assertEquals(Items.DIAMOND_PICKAXE, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getDamageValue() > 0);
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getDamageValue() < 100);
    }

    @Test
    public void tryMendTool_UndamagedTool_NothingChanges(MinecraftServer server) {
        // Arrange
        Fluid xp = EIOFluids.XP_JUICE.source().get();
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(xp, FluidType.BUCKET_VOLUME * 10));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, getDamagedPickaxe(server, 0, true));

        // Act
        InternalTankTasks.tryMendTool(server.registryAccess(), fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 10, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the tool stayed in the input
        Assertions.assertEquals(Items.DIAMOND_PICKAXE, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void tryMendTool_NoMending_NothingChanges(MinecraftServer server) {
        // Arrange
        Fluid xp = EIOFluids.XP_JUICE.source().get();
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(xp, FluidType.BUCKET_VOLUME * 10));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, getDamagedPickaxe(server, 100, false));

        // Act
        InternalTankTasks.tryMendTool(server.registryAccess(), fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 10, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the tool stayed in the input
        Assertions.assertEquals(Items.DIAMOND_PICKAXE, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void tryMendTool_NonXpFluid_NothingChanges(MinecraftServer server) {
        // Arrange
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME * 10));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, getDamagedPickaxe(server, 100, true));

        // Act
        InternalTankTasks.tryMendTool(server.registryAccess(), fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 10, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the tool stayed in the input
        Assertions.assertEquals(Items.DIAMOND_PICKAXE, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertTrue(DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).isEmpty());
    }

    @Test
    public void tryMendTool_OutputOccupied_NothingChanges(MinecraftServer server) {
        // Arrange
        Fluid xp = EIOFluids.XP_JUICE.source().get();
        MachineFluidHandler fluidStorage = createFluidStorage(FluidType.BUCKET_VOLUME * 10);
        TANK_SLOT.setFluid(fluidStorage, new FluidStack(xp, FluidType.BUCKET_VOLUME * 10));

        MachineInventory itemStorage = createItemStorage();
        DRAIN_INPUT_SLOT.setStackInSlot(itemStorage, getDamagedPickaxe(server, 100, true));
        DRAIN_OUTPUT_SLOT.setStackInSlot(itemStorage, new ItemStack(Items.STICK));

        // Act
        InternalTankTasks.tryMendTool(server.registryAccess(), fluidStorage, TANK_SLOT, itemStorage, DRAIN_INPUT_SLOT, DRAIN_OUTPUT_SLOT);

        // Assert
        Assertions.assertEquals(FluidType.BUCKET_VOLUME * 10, TANK_SLOT.getFluidAmount(fluidStorage));

        // Ensure the tool stayed in the input
        Assertions.assertEquals(Items.DIAMOND_PICKAXE, DRAIN_INPUT_SLOT.getItemStack(itemStorage).getItem());
        Assertions.assertEquals(Items.STICK, DRAIN_OUTPUT_SLOT.getItemStack(itemStorage).getItem());
    }

    private ItemStack getDamagedPickaxe(MinecraftServer server, int damage, boolean mending) {
        var stack = new ItemStack(Items.DIAMOND_PICKAXE);
        if (mending) {
            var mendingEnchantment = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.MENDING);
            stack.enchant(mendingEnchantment, 1);
        }
        stack.setDamageValue(damage);
        return stack;
    }

    // endregion

}
