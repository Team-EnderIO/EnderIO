package com.enderio.machines.common.blocks.fluid_tank;

import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.io.fluid.TankAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class InternalTankTasks {

    // TODO: enable fluid tanks to receive stackable fluid containers
    public static <T extends MachineBlockEntity & FluidTankUser> void fillInternal(
        T blockEntity,
        TankAccess tank,
        SingleSlotAccess fluidFillInput,
        SingleSlotAccess fluidFillOutput
    ) {
        ItemStack inputItem = fluidFillInput.getItemStack(blockEntity);
        ItemStack outputItem = fluidFillOutput.getItemStack(blockEntity);

        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() instanceof BucketItem filledBucket) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET
                    && outputItem.getCount() < outputItem.getMaxStackSize())) {

                    int filled = tank.fill(blockEntity, new FluidStack(filledBucket.content, FluidType.BUCKET_VOLUME),
                        IFluidHandler.FluidAction.SIMULATE);

                    if (filled == FluidType.BUCKET_VOLUME) {
                        tank.fill(blockEntity, new FluidStack(filledBucket.content, FluidType.BUCKET_VOLUME),
                            IFluidHandler.FluidAction.EXECUTE);

                        inputItem.shrink(1);
                        fluidFillOutput.insertItem(blockEntity, Items.BUCKET.getDefaultInstance(), false);
                    }
                }
            } else {
                IFluidHandlerItem fluidHandlerItem = inputItem.getCapability(Capabilities.FluidHandler.ITEM);
                if (fluidHandlerItem != null && outputItem.isEmpty()) {
                    int filled = FluidUtil.tryFluidTransfer(
                        blockEntity.getFluidHandler(),
                        fluidHandlerItem,
                        tank.getFluidAmount(blockEntity),
                        true
                    ).getAmount();

                    if (filled > 0) {
                        fluidFillOutput.setStackInSlot(blockEntity, fluidHandlerItem.getContainer());
                        fluidFillInput.setStackInSlot(blockEntity, ItemStack.EMPTY);
                    }
                }
            }
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
        ItemStack outputItem = fluidDrainOutput.getItemStack(blockEntity);

        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() == Items.BUCKET) {
                if (!tank.getFluid(blockEntity).isEmpty()) {
                    FluidStack stack = tank.drain(blockEntity, FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
                    if (stack.getAmount() == FluidType.BUCKET_VOLUME &&
                        (outputItem.isEmpty() || (outputItem.getItem() == stack.getFluid().getBucket()
                            && outputItem.getCount() < outputItem.getMaxStackSize()))) {

                        tank.drain(blockEntity, FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
                        inputItem.shrink(1);
                        if (outputItem.isEmpty()) {
                            fluidDrainOutput.setStackInSlot(blockEntity, stack.getFluid().getBucket().getDefaultInstance());
                        } else {
                            outputItem.grow(1);
                        }
                    }
                }
            } else {
                IFluidHandlerItem fluidHandlerItem = inputItem.getCapability(Capabilities.FluidHandler.ITEM);
                if (fluidHandlerItem != null && outputItem.isEmpty()) {
                    int filled = FluidUtil.tryFluidTransfer(
                        fluidHandlerItem,
                        blockEntity.getFluidHandler(), // méthode à définir dans ton interface
                        tank.getFluidAmount(blockEntity),
                        true
                    ).getAmount();

                    if (filled > 0) {
                        fluidDrainOutput.setStackInSlot(blockEntity, fluidHandlerItem.getContainer());
                        fluidDrainInput.setStackInSlot(blockEntity, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public static <T extends MachineBlockEntity & FluidTankUser> void tryMendTool(
        T blockEntity,
        TankAccess tank,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        FluidStack fluid = tank.getFluid(blockEntity);

        if (!fluid.isEmpty() && fluid.is(EIOTags.Fluids.EXPERIENCE)
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
