package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.common.capability.EnderFluidUtil;
import com.enderio.enderio.foundation.attachment.FluidTankUser;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.fluid.TankAccess;
import com.enderio.enderio.foundation.util.ExperienceUtil;
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
        SingleSlotAccess fluidFillInput,
        SingleSlotAccess fluidFillOutput) {

        ItemStack inputItem = fluidFillInput.getItemStack(blockEntity);
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
        // We act here because we're acting on a copy of the input.
        FluidStack availableFluid = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.EXECUTE);

        // See if we can insert this fluid into the block's tank.
        int filled = tank.fill(blockEntity, availableFluid, IFluidHandler.FluidAction.SIMULATE);
        if (filled <= 0) {
            return;
        }

        // Get the resulting emptied container
        ItemStack resultStack = fluidHandlerItem.getContainer();

        // If this is the only input, and it's not fully empty we will retain it in the input slot, so long as the
        //  destination tank is not full
        if (inputItem.getCount() == 1 && !EnderFluidUtil.isEmpty(fluidHandlerItem) && !tank.isFull(blockEntity)) {
            tank.fill(blockEntity, availableFluid.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
            fluidFillInput.setStackInSlot(blockEntity, resultStack);
        } else if (!fluidFillInput.extractItem(blockEntity, 1, true).isEmpty() &&
            fluidFillOutput.insertItem(blockEntity, resultStack, true).isEmpty()) {

            tank.fill(blockEntity, availableFluid.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
            fluidFillInput.extractItem(blockEntity, 1, false);
            fluidFillOutput.insertItem(blockEntity, resultStack, false);
        }
    }

    // TODO: enable fluid tanks to receive stackable fluid containers
    public static <T extends MachineBlockEntity & FluidTankUser> void drainInternal(
        T blockEntity,
        TankAccess tank,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        ItemStack inputItem = fluidDrainInput.getItemStack(blockEntity);
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
        FluidStack availableFluid = tank.drain(blockEntity, Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

        // See if we can insert this fluid into the container
        int filled = fluidHandlerItem.fill(availableFluid, IFluidHandler.FluidAction.EXECUTE);
        if (filled <= 0) {
            return;
        }

        // Get the resulting filled item
        ItemStack resultStack = fluidHandlerItem.getContainer();

        // If this is the only input, and it isn't completely full, and the destination still has fluid, we will retain it
        if (inputItem.getCount() == 1 && !EnderFluidUtil.isFull(fluidHandlerItem) && !tank.isEmpty(blockEntity)) {
            tank.drain(blockEntity, availableFluid.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
        } else if (!fluidDrainInput.extractItem(blockEntity, 1, true).isEmpty() &&
            fluidDrainOutput.insertItem(blockEntity, resultStack, true).isEmpty()) {

            tank.drain(blockEntity, availableFluid.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
            fluidDrainInput.extractItem(blockEntity, 1, false);
            fluidDrainOutput.insertItem(blockEntity, resultStack, false);
        }
    }

    public static <T extends MachineBlockEntity & FluidTankUser> void tryMendTool(
        T blockEntity,
        TankAccess tank,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        FluidStack fluid = tank.getFluid(blockEntity);

        if (!fluid.isEmpty() && fluid.is(Tags.Fluids.EXPERIENCE)
            && fluidDrainOutput.getItemStack(blockEntity).isEmpty()) {

            ItemStack tool = fluidDrainInput.getItemStack(blockEntity);

            var enchantmentsRecipe = blockEntity.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var mendingEnchantment = enchantmentsRecipe.getOrThrow(Enchantments.MENDING);

            if (tool.isDamageableItem() && tool.getEnchantmentLevel(mendingEnchantment) > 0) {

                ItemStack repairedTool = tool.copy();

                int damage = tool.getDamageValue();
                int xpAmount = (int) Math.floor(damage / tool.getXpRepairRatio());
                int fluidAmount = xpAmount * ExperienceUtil.EXP_TO_FLUID;

                FluidStack drainedXp = tank.drain(blockEntity, fluidAmount, IFluidHandler.FluidAction.EXECUTE);
                int repairAmount = (int) Math.floor(drainedXp.getAmount() * tool.getXpRepairRatio() / ExperienceUtil.EXP_TO_FLUID);
                repairedTool.setDamageValue(Math.max(0, damage - repairAmount));

                fluidDrainInput.setStackInSlot(blockEntity, ItemStack.EMPTY);
                fluidDrainOutput.setStackInSlot(blockEntity, repairedTool);
            }
        }
    }
}
