package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderResourceUtil;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.RangedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class InternalTankTasks {

    public static int fillUsingItem(
        ResourceStorage<FluidResource> fluidStorage,
        ResourceSlotId<FluidResource> tankSlot,
        ItemAccess itemToDrain
    ) {
        if (itemToDrain.getResource().isEmpty()) {
            return 0;
        }

        // We should only drain a single item each time
        var itemToDrainOneByOne = itemToDrain.oneByOne();

        var fluidHandlerItem = itemToDrainOneByOne.getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandlerItem == null || ResourceHandlerUtil.isEmpty(fluidHandlerItem)) {
            return 0;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int filled = EnderResourceUtil.moveInto(fluidHandlerItem, fluidStorage, tankSlot, _ -> true, Integer.MAX_VALUE, transaction);
            transaction.commit();
            return filled;
        }
    }

    public static int drainIntoItem(
        ResourceStorage<FluidResource> fluidStorage,
        ResourceSlotId<FluidResource> tankSlot,
        ItemAccess itemToFill
    ) {
        if (itemToFill.getResource().isEmpty()) {
            return 0;
        }

        // We should only fill a single item each time
        var itemToDrainOneByOne = itemToFill.oneByOne();

        var fluidHandlerItem = itemToDrainOneByOne.getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandlerItem == null || ResourceHandlerUtil.isFull(fluidHandlerItem)) {
            return 0;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            int drained = ResourceHandlerUtil.move(
                RangedResourceHandler.ofSingleIndex(fluidStorage, tankSlot.index(fluidStorage.layout())),
                fluidHandlerItem,
                fr -> true,
                Integer.MAX_VALUE,
                transaction
            );

            transaction.commit();
            return drained;
        }
    }

    public static void tryMendTool(
        HolderLookup.Provider registries,
        ResourceStorage<FluidResource> fluidStorage,
        SingleResourceSlotKey<FluidResource> tankSlot,
        ItemAccess tool
    ) {
        FluidResource resource = fluidStorage.getResource(tankSlot);
        if (resource.isEmpty() || !resource.is(Tags.Fluids.EXPERIENCE)) {
            return;
        }

        // Find mending enchantment
        var enchantmentsRecipe = registries.lookupOrThrow(Registries.ENCHANTMENT);
        var mendingEnchantment = enchantmentsRecipe.getOrThrow(Enchantments.MENDING);

        // Note this is a copy, we can act on it then return it to the ItemAccess
        var toolStack = tool.getResource().toStack(tool.getAmount());

        if (!toolStack.isDamageableItem() || toolStack.getEnchantmentLevel(mendingEnchantment) <= 0) {
            return;
        }

        int damage = toolStack.getDamageValue();
        int xpAmount = (int) Math.floor(damage / toolStack.getXpRepairRatio());
        int fluidAmount = xpAmount * ExperienceUtil.EXP_TO_FLUID;

        try (Transaction transaction = Transaction.openRoot()) {
            int extractedExperience = fluidStorage.extract(tankSlot, resource, fluidAmount, transaction);
            if (extractedExperience <= 0) {
                return;
            }

            int repairAmount = (int) Math.floor(extractedExperience * toolStack.getXpRepairRatio() / ExperienceUtil.EXP_TO_FLUID);

            toolStack.setDamageValue(Math.max(0, damage - repairAmount));

            int exchanged = tool.exchange(ItemResource.of(toolStack), toolStack.getCount(), transaction);
            if (exchanged != toolStack.getCount()) {
                return;
            }

            transaction.commit();
        }
    }
}
