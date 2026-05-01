package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.util.EnderResourceUtil;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

public class InternalTankTasks {

    private record SingleItemOutputAccess(MachineInventory inventory, SingleSlotAccess inputSlot, SingleSlotAccess outputSlot) implements ItemAccess {
        @Override
        public ItemResource getResource() {
            return inputSlot.getResource(inventory);
        }

        @Override
        public int getAmount() {
            return Math.min(1, inventory.getAmountAsInt(inputSlot.getIndex()));
        }

        @Override
        public int insert(ItemResource resource, int amount, TransactionContext transaction) {
            // NeoForge currently requires a non-empty returned item, but may later allow null
            // to mean "the source item was consumed without a remainder".
            if (resource == null) {
                return Math.min(amount, 1);
            }

            return outputSlot.insert(inventory, resource, Math.min(amount, 1), transaction);
        }

        @Override
        public int extract(ItemResource resource, int amount, TransactionContext transaction) {
            return inventory.extract(inputSlot.getIndex(), resource, Math.min(amount, 1), transaction);
        }
    }

    private static int getSingleItemFluidAmount(ItemAccess itemAccess) {
        var fluidHandlerItem = itemAccess.getCapability(Capabilities.Fluid.ITEM);
        for (int i = 0; i < fluidHandlerItem.size(); i++) {
            FluidResource resource = fluidHandlerItem.getResource(i);
            int amount = fluidHandlerItem.getAmountAsInt(i);

            if (!resource.isEmpty() && amount > 0) {
                return amount;
            }
        }

        return 0;
    }

    private static <T extends MachineBlockEntity> boolean tryFillNonStandardBucketItem(
        T blockEntity,
        ResourceStorage<FluidResource> fluidStorage,
        ResourceSlotId<FluidResource> tankSlot,
        SingleSlotAccess fluidFillInput,
        SingleSlotAccess fluidFillOutput
    ) {
        ItemStack inputItem = fluidFillInput.getItemStack(blockEntity);
        if (!(inputItem.getItem() instanceof BucketItem) || inputItem.getItem().getClass() == BucketItem.class) {
            return false;
        }

        ItemAccess itemAccess = new SingleItemOutputAccess(blockEntity.getInventory(), fluidFillInput, fluidFillOutput);
        var fluidHandlerItem = itemAccess.getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandlerItem == null) {
            return false;
        }

        int containedAmount = getSingleItemFluidAmount(itemAccess);
        if (containedAmount <= 0) {
            return false;
        }

        // Non-standard BucketItem subclasses can advertise custom amounts or remainders through their capability.
        // Use the capability exchange path so their returned item is preserved in the output slot.
        try (Transaction transaction = Transaction.openRoot()) {
            int moved = EnderResourceUtil.moveInto(fluidHandlerItem, fluidStorage, tankSlot, fr -> true, containedAmount, transaction);
            if (moved == containedAmount) {
                transaction.commit();
            }
        }

        return true;
    }

    // TODO: enable fluid tanks to receive stackable fluid containers
    public static <T extends MachineBlockEntity> void fillInternal(
        T blockEntity,
        ResourceStorage<FluidResource> fluidStorage,
        ResourceSlotId<FluidResource> tankSlot,
        SingleSlotAccess fluidFillInput,
        SingleSlotAccess fluidFillOutput
    ) {
        ItemStack inputItem = fluidFillInput.getItemStack(blockEntity);
        ItemStack outputItem = fluidFillOutput.getItemStack(blockEntity);

        if (!inputItem.isEmpty()) {
            if (tryFillNonStandardBucketItem(blockEntity, fluidStorage, tankSlot, fluidFillInput, fluidFillOutput)) {
                return;
            }

            if (inputItem.getItem() instanceof BucketItem filledBucket && !filledBucket.content.isSame(Fluids.EMPTY)) {
                if (outputItem.isEmpty() || (outputItem.getItem() == Items.BUCKET
                    && outputItem.getCount() < outputItem.getMaxStackSize())) {

                    try (Transaction transaction = Transaction.openRoot()) {
                        int filled = fluidStorage.internalInsert(tankSlot, FluidResource.of(filledBucket.content), FluidType.BUCKET_VOLUME, transaction);
                        if (filled != FluidType.BUCKET_VOLUME) {
                            return;
                        }

                        int bucketsReturned = fluidFillOutput.insert(blockEntity, ItemResource.of(Items.BUCKET), 1, transaction);
                        if (bucketsReturned != 1) {
                            return;
                        }

                        inputItem.shrink(1);
                        transaction.commit();
                    }
                }
            } else {
                var fluidHandlerItem = ItemAccess.forStack(inputItem).getCapability(Capabilities.Fluid.ITEM);
                if (fluidHandlerItem != null && outputItem.isEmpty()) {

                    try (Transaction transaction = Transaction.openRoot()) {
                        int moved = EnderResourceUtil.moveInto(fluidHandlerItem, fluidStorage, tankSlot, fr -> true, Integer.MAX_VALUE, transaction);
                        if (moved > 0) {
                            // TODO: Should we wait until either internal buffer is full or item is empty?
                            transaction.commit();
                            fluidFillOutput.setStackInSlot(blockEntity, inputItem);
                            fluidFillInput.setStackInSlot(blockEntity, ItemStack.EMPTY);
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
        ResourceSlotId<FluidResource> tankSlot,
        SingleSlotAccess fluidDrainInput,
        SingleSlotAccess fluidDrainOutput
    ) {
        ItemStack inputItem = fluidDrainInput.getItemStack(blockEntity);
        ItemStack outputItem = fluidDrainOutput.getItemStack(blockEntity);

        if (!inputItem.isEmpty()) {
            if (inputItem.getItem() == Items.BUCKET) {
                if (outputItem.isEmpty() || (outputItem.getCount() < outputItem.getMaxStackSize())) {
                    if (fluidStorage.getAmountAsInt(tankSlot) > 0) {

                        try (Transaction transaction = Transaction.openRoot()) {
                            var resource = fluidStorage.getResource(tankSlot);
                            if (resource.isEmpty()) {
                                return;
                            }

                            // Ensure there's a bucket to be given to the player.
                            if (resource.getFluid().getBucket() == Items.AIR) {
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
                }
            } else {
                var fluidHandlerItem = ItemAccess.forStack(inputItem).getCapability(Capabilities.Fluid.ITEM);
                if (fluidHandlerItem != null && outputItem.isEmpty()) {

                    try (Transaction transaction = Transaction.openRoot()) {
                        int moved = ResourceHandlerUtil.move(fluidStorage, fluidHandlerItem, fr -> true, Integer.MAX_VALUE, transaction);
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
        ResourceSlotId<FluidResource> tankSlot,
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
