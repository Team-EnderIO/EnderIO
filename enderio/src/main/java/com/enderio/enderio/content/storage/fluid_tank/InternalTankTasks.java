package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.common.capability.EnderFluidUtil;
import com.enderio.enderio.foundation.attachment.FluidTankUser;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.fluid.MachineFluidHandler;
import com.enderio.enderio.foundation.io.fluid.TankAccess;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class InternalTankTasks {

    public static <T extends MachineBlockEntity & FluidTankUser> void fillInternal(
        T blockEntity,
        TankAccess tank,
        SingleSlotAccess input,
        SingleSlotAccess output) {
        fillInternal(blockEntity.getFluidHandler(), tank, blockEntity.getInventory(), input, output);
    }

    public static void fillInternal(
        MachineFluidHandler fluids,
        TankAccess tank,
        MachineInventory inventory,
        SingleSlotAccess fluidFillInput,
        SingleSlotAccess fluidFillOutput) {

        ItemStack inputItem = fluidFillInput.getItemStack(inventory);
        if (inputItem.isEmpty()) {
            return;
        }

        // Only ever handle a single item at a time.
        ItemStack singleInputItem = inputItem.copyWithCount(1);
        IFluidHandlerItem fluidHandlerItem = singleInputItem.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem == null) {
            return;
        }

        // See what fluid is available to drain from the item.
        // This is a simulation, the actual drain is performed below once we know how much the tank will accept.
        FluidStack availableFluid = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (availableFluid.isEmpty()) {
            return;
        }

        // See if we can insert this fluid into the block's tank.
        int filled = tank.fill(fluids, availableFluid, IFluidHandler.FluidAction.SIMULATE);
        if (filled <= 0) {
            return;
        }

        // Drain the fluid from the item, only taking what the tank can hold.
        // We act here because we're acting on a copy of the input.
        FluidStack drainedFluid = fluidHandlerItem.drain(availableFluid.copyWithAmount(filled),
            IFluidHandler.FluidAction.EXECUTE);
        if (drainedFluid.isEmpty()) {
            return;
        }

        // Get the resulting (drained) container
        ItemStack resultStack = fluidHandlerItem.getContainer();

        // If this is the only input and it still holds fluid (partial fill) we retain it in the input slot
        // so the remainder can be moved once the tank has room.
        if (inputItem.getCount() == 1 && !EnderFluidUtil.isEmpty(fluidHandlerItem)) {
            tank.fill(fluids, drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            fluidFillInput.setStackInSlot(inventory, resultStack);
        } else if (!fluidFillInput.extractItem(inventory, 1, true).isEmpty()
            && fluidFillOutput.insertItem(inventory, resultStack, true).isEmpty()) {

            tank.fill(fluids, drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            fluidFillInput.extractItem(inventory, 1, false);
            fluidFillOutput.insertItem(inventory, resultStack, false);
        }
    }

    // TODO: enable fluid tanks to receive stackable fluid containers
    public static <T extends MachineBlockEntity & FluidTankUser> void drainInternal(
        T blockEntity,
        TankAccess tank,
        SingleSlotAccess input,
        SingleSlotAccess output) {
        drainInternal(blockEntity.getFluidHandler(), tank, blockEntity.getInventory(), input, output);
    }

    public static void drainInternal(
        MachineFluidHandler fluids,
        TankAccess tank,
        MachineInventory inventory,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        ItemStack inputItem = fluidDrainInput.getItemStack(inventory);
        if (inputItem.isEmpty()) {
            return;
        }

        // Only ever handle a single item at a time.
        ItemStack singleInputItem = inputItem.copyWithCount(1);
        IFluidHandlerItem fluidHandlerItem = singleInputItem.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandlerItem == null) {
            return;
        }

        // See what fluid the tank can drain into the item
        FluidStack availableFluid = tank.drain(fluids, Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if (availableFluid.isEmpty()) {
            return;
        }

        // See if we can insert this fluid into the container
        int filled = fluidHandlerItem.fill(availableFluid, IFluidHandler.FluidAction.EXECUTE);
        if (filled <= 0) {
            return;
        }

        // Get the resulting filled item
        ItemStack resultStack = fluidHandlerItem.getContainer();

        // If this is the only input and it isn't completely full we will retain it in the input slot.
        if (inputItem.getCount() == 1 && !EnderFluidUtil.isFull(fluidHandlerItem)) {
            tank.drain(fluids, availableFluid.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
            fluidDrainInput.setStackInSlot(inventory, resultStack);
        } else if (!fluidDrainInput.extractItem(inventory, 1, true).isEmpty() &&
            fluidDrainOutput.insertItem(inventory, resultStack, true).isEmpty()) {

            tank.drain(fluids, availableFluid.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
            fluidDrainInput.extractItem(inventory, 1, false);
            fluidDrainOutput.insertItem(inventory, resultStack, false);
        }
    }

    public static <T extends MachineBlockEntity & FluidTankUser> void tryMendTool(
        T blockEntity,
        TankAccess tank,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        tryMendTool(blockEntity.getLevel().registryAccess(), blockEntity.getFluidHandler(), tank,
            blockEntity.getInventory(), fluidDrainInput, fluidDrainOutput);
    }

    public static void tryMendTool(
        HolderLookup.Provider registries,
        MachineFluidHandler fluids,
        TankAccess tank,
        MachineInventory inventory,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        FluidStack fluid = tank.getFluid(fluids);
        if (fluid.isEmpty() || !fluid.is(Tags.Fluids.EXPERIENCE) || !fluidDrainOutput.getItemStack(inventory).isEmpty()) {
            return;
        }

        ItemStack tool = fluidDrainInput.getItemStack(inventory);
        if (tool.isEmpty() || !tool.isDamageableItem()) {
            return;
        }

        var enchantmentsRecipe = registries.lookupOrThrow(Registries.ENCHANTMENT);
        var mendingEnchantment = enchantmentsRecipe.getOrThrow(Enchantments.MENDING);
        if (tool.getEnchantmentLevel(mendingEnchantment) <= 0) {
            return;
        }

        int damage = tool.getDamageValue();
        int xpAmount = (int) Math.floor(damage / tool.getXpRepairRatio());
        int fluidAmount = xpAmount * ExperienceUtil.EXP_TO_FLUID;
        if (fluidAmount <= 0) {
            // Nothing to repair, leave the tank and tool untouched.
            return;
        }

        FluidStack drainedXp = tank.drain(fluids, fluidAmount, IFluidHandler.FluidAction.EXECUTE);
        if (drainedXp.isEmpty()) {
            return;
        }

        ItemStack repairedTool = tool.copy();
        int repairAmount = (int) Math.floor(drainedXp.getAmount() * tool.getXpRepairRatio() / ExperienceUtil.EXP_TO_FLUID);
        repairedTool.setDamageValue(Math.max(0, damage - repairAmount));

        fluidDrainInput.setStackInSlot(inventory, ItemStack.EMPTY);
        fluidDrainOutput.setStackInSlot(inventory, repairedTool);
    }
}
