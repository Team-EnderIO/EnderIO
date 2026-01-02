package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderResourceUtil;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class NewInternalTankTasks {

    public static void fillInternal(
        ResourceStorage<FluidResource> fluidStorage,
        ResourceSlotId<FluidResource> tankSlot,
        ResourceStorage<ItemResource> itemStorage,
        ResourceSlotId<ItemResource> fluidFillInputSlot,
        ResourceSlotId<ItemResource> fluidFillOutputSlot
    ) {
        ItemAccess input = EnderResourceUtil.getItemAccessStrict(itemStorage, fluidFillInputSlot);

        if (!input.getResource().isEmpty()) {
            var fluidHandlerItem = input.getCapability(Capabilities.Fluid.ITEM);
            if (fluidHandlerItem != null) {
                if (ResourceHandlerUtil.isEmpty(fluidHandlerItem)) {
                    // Move empty item straight to the output
                    EnderResourceUtil.tryMoveItem(itemStorage, fluidFillInputSlot, fluidFillOutputSlot, null);
                    return;
                }

                try (Transaction transaction = Transaction.openRoot()) {
                    int moved = EnderResourceUtil.moveInto(fluidHandlerItem, fluidStorage, tankSlot, fr -> true, Integer.MAX_VALUE, transaction);
                    if (moved > 0) {
                        // Only commit if we managed to move the input to the output
                        if (EnderResourceUtil.tryMoveItem(itemStorage, fluidFillInputSlot, fluidFillOutputSlot, transaction)) {
                            transaction.commit();
                        }
                    }
                }
            }
        }
    }

    // TODO: enable fluid tanks to receive stackable fluid containers
    public static <T extends MachineBlockEntity> void drainInternal(
        T blockEntity,
        ResourceStorage<FluidResource> fluidStorage,
        SingleResourceSlotKey<FluidResource> tankSlot,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        ItemStack inputItem = fluidDrainInput.getItemStack(blockEntity);
        ItemStack outputItem = fluidDrainOutput.getItemStack(blockEntity);

        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() == Items.BUCKET) {
                if (fluidStorage.getAmountAsInt(tankSlot) > 0) {

                    try (Transaction transaction = Transaction.openRoot()) {
                        var resource = fluidStorage.getResource(tankSlot);
                        if (resource.isEmpty()) {
                            return;
                        }

                        int extracted = fluidStorage.internalExtract(tankSlot, resource, FluidType.BUCKET_VOLUME, transaction);
                        if (extracted != FluidType.BUCKET_VOLUME) {
                            return;
                        }

                        transaction.commit();
                        inputItem.shrink(1);
                        if (outputItem.isEmpty()) {
                            fluidDrainOutput.setStackInSlot(blockEntity, resource.getFluid().getBucket().getDefaultInstance());
                        } else {
                            outputItem.grow(1);
                        }
                    }
                }
            } else {
                var fluidHandlerItem = ItemAccess.forStack(inputItem).getCapability(Capabilities.Fluid.ITEM);
                if (fluidHandlerItem != null && outputItem.isEmpty()) {

                    try (Transaction transaction = Transaction.openRoot()) {
                        int moved = ResourceHandlerUtil.move(fluidHandlerItem, fluidStorage, fr -> true, Integer.MAX_VALUE, transaction);
                        if (moved > 0) {
                            transaction.commit();
                            fluidDrainOutput.setStackInSlot(blockEntity, inputItem);
                            fluidDrainInput.setStackInSlot(blockEntity, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }
    }

    public static <T extends MachineBlockEntity> void tryMendTool(
        T blockEntity,
        ResourceStorage<FluidResource> fluidStorage,
        SingleResourceSlotKey<FluidResource> tankSlot,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        FluidResource resource = fluidStorage.getResource(tankSlot);

        if (!resource.isEmpty() && resource.is(Tags.Fluids.EXPERIENCE)
            && fluidDrainOutput.getItemStack(blockEntity).isEmpty()) {

            ItemStack tool = fluidDrainInput.getItemStack(blockEntity);

            var enchantmentsRecipe = blockEntity.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var mendingEnchantment = enchantmentsRecipe.getOrThrow(Enchantments.MENDING);

            if (tool.isDamageableItem() && tool.getEnchantmentLevel(mendingEnchantment) > 0) {
                ItemStack repairedTool = tool.copy();

                int damage = tool.getDamageValue();
                int xpAmount = (int) Math.floor(damage / tool.getXpRepairRatio());
                int fluidAmount = xpAmount * ExperienceUtil.EXP_TO_FLUID;

                try (Transaction transaction = Transaction.openRoot()) {
                    int extracted = fluidStorage.internalExtract(tankSlot, resource, fluidAmount, transaction);
                    if (extracted <= 0) {
                        return;
                    }

                    transaction.commit();
                    int repairAmount = (int) Math.floor(extracted * tool.getXpRepairRatio() / ExperienceUtil.EXP_TO_FLUID);
                    repairedTool.setDamageValue(Math.max(0, damage - repairAmount));

                    fluidDrainInput.setStackInSlot(blockEntity, ItemStack.EMPTY);
                    fluidDrainOutput.setStackInSlot(blockEntity, repairedTool);
                }
            }
        }
    }

}
