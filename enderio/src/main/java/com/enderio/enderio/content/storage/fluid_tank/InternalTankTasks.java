package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderResourceUtil;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class InternalTankTasks {

    public static void fillInternal(
        ResourceStorage<FluidResource> fluidStorage,
        ResourceSlotId<FluidResource> tankSlot,
        ResourceStorage<ItemResource> itemStorage,
        ResourceSlotId<ItemResource> fluidFillInputSlot,
        ResourceSlotId<ItemResource> fluidFillOutputSlot
    ) {
        ItemAccess input = EnderResourceUtil.getItemAccessStrict(itemStorage, fluidFillInputSlot);
        if (input.getResource().isEmpty()) {
            return;
        }

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

    // TODO: enable fluid tanks to receive stackable fluid containers
    public static void drainInternal(
        ResourceStorage<FluidResource> fluidStorage,
        ResourceSlotId<FluidResource> tankSlot,
        ResourceStorage<ItemResource> itemStorage,
        ResourceSlotId<ItemResource> fluidDrainInputSlot,
        ResourceSlotId<ItemResource> fluidDrainOutputSlot
    ) {
        ItemAccess input = EnderResourceUtil.getItemAccessStrict(itemStorage, fluidDrainInputSlot);
        if (input.getResource().isEmpty()) {
            return;
        }

        var fluidHandlerItem = input.getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandlerItem == null) {
            return;
        }

        // If the item is already full, move it to the output
        boolean isFull = true;
        for (int i = 0; i < fluidHandlerItem.size(); i++) {
            if (fluidHandlerItem.getAmountAsLong(i) < fluidHandlerItem.getCapacityAsLong(i, fluidHandlerItem.getResource(i))) {
                isFull = false;
                break;
            }
        }

        if (isFull) {
            EnderResourceUtil.tryMoveItem(itemStorage, fluidDrainInputSlot, fluidDrainOutputSlot, null);
            return;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int moved = ResourceHandlerUtil.move(
                RangedResourceHandler.ofSingleIndex(fluidStorage, tankSlot.index(fluidStorage.layout())),
                fluidHandlerItem,
                fr -> true,
                Integer.MAX_VALUE,
                transaction
            );

            if (moved > 0) {
                // Ensure we've not generated 'air' (i.e. no bucket for fluid).
                // If we have, do not commit to extracting the resource.
                if (itemStorage.getResource(fluidDrainInputSlot).isEmpty()) {
                    return;
                }

                // Only commit if we managed to move the input to the output
                if (EnderResourceUtil.tryMoveItem(itemStorage, fluidDrainInputSlot, fluidDrainOutputSlot, transaction)) {
                    transaction.commit();
                }
            }
        }
    }

    public static <T extends MachineBlockEntity> void tryMendTool(
        HolderLookup.Provider registries,
        ResourceStorage<FluidResource> fluidStorage,
        SingleResourceSlotKey<FluidResource> tankSlot,
        ResourceStorage<ItemResource> itemStorage,
        SingleResourceSlotKey<ItemResource> fluidDrainInput,
        SingleResourceSlotKey<ItemResource> fluidDrainOutput
    ) {
        FluidResource resource = fluidStorage.getResource(tankSlot);
        if (!resource.isEmpty() && resource.is(Tags.Fluids.EXPERIENCE)) {
            return;
        }

        // Get the tool in the input
        var tool = EnderResourceUtil.getItemStack(itemStorage, fluidDrainOutput);

        // Find mending enchantment
        var enchantmentsRecipe = registries.lookupOrThrow(Registries.ENCHANTMENT);
        var mendingEnchantment = enchantmentsRecipe.getOrThrow(Enchantments.MENDING);

        if (!tool.isDamageableItem() || tool.getEnchantmentLevel(mendingEnchantment) <= 0) {
            return;
        }

        ItemStack repairedTool = tool.copy();

        int damage = tool.getDamageValue();
        int xpAmount = (int) Math.floor(damage / tool.getXpRepairRatio());
        int fluidAmount = xpAmount * ExperienceUtil.EXP_TO_FLUID;

        try (Transaction transaction = Transaction.openRoot()) {
            int extracted = fluidStorage.extract(tankSlot, resource, fluidAmount, transaction);
            if (extracted <= 0) {
                return;
            }

            int repairAmount = (int) Math.floor(extracted * tool.getXpRepairRatio() / ExperienceUtil.EXP_TO_FLUID);
            repairedTool.setDamageValue(Math.max(0, damage - repairAmount));

            transaction.commit();

            itemStorage.set(fluidDrainInput, ItemResource.EMPTY, 0);
            itemStorage.set(fluidDrainOutput, ItemResource.of(repairedTool), repairedTool.count());
        }
    }

}
