package com.enderio.enderio.foundation.io.fluid;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.Optional;

/**
 * An interface that block entities may implement in order to interact with items support IFluidHandlerItem
 * Adapted from Mekanism's FluidUtils.
 */
public interface FluidItemInteractive {

    // TODO: 1.21.11: Check this still works as expected - I'd like to see gametests or unit tests too.
    // Requires direct tank access which is undesirable. MachineFluidHandler would be better to for multi-tank block.
    default boolean handleFluidItemInteraction(Player player, InteractionHand hand, ItemStack itemStack, ResourceStorage<FluidResource> fluidStorage,
        SingleResourceSlotKey<FluidResource> tankSlot) {
        ItemStack copyStack = itemStack.copyWithCount(1);
        var itemFluidHandler = ItemAccess.forStack(copyStack).getCapability(Capabilities.Fluid.ITEM);
        if (itemFluidHandler == null) {
            return false;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            FluidResource resourceFromItem = fluidStorage.getResource(tankSlot);
            int amountAvailable = 0;
            try (Transaction subTransaction = Transaction.open(transaction)) {
                if (resourceFromItem.isEmpty()) {
                    // If we have no storage, see what's in the item.
                    ResourceStack<FluidResource> resourceInItem = ResourceHandlerUtil.extractFirst(itemFluidHandler, fr -> true, Integer.MAX_VALUE,
                        subTransaction);
                    if (resourceInItem != null) {
                        resourceFromItem = resourceInItem.resource();
                        amountAvailable = resourceInItem.amount();
                    }
                } else {
                    amountAvailable = itemFluidHandler.extract(resourceFromItem, Integer.MAX_VALUE, subTransaction);
                }
            }

            // If the item is empty, attempt to fill it.
            if (resourceFromItem.isEmpty() || amountAvailable <= 0) {
                FluidResource resourceInMachine = fluidStorage.getResource(tankSlot);
                if (resourceInMachine.isEmpty()) {
                    return false;
                }

                int maxExtract;
                try (Transaction subTransaction = Transaction.open(transaction)) {
                    maxExtract = fluidStorage.extract(resourceFromItem, Integer.MAX_VALUE, subTransaction);
                }

                if (maxExtract <= 0) {
                    return false;
                }

                // Insert as much of the fluid as possible.
                // Simulate this in creative mode as we won't modify the stack in-hand.
                int inserted;
                if (player.isCreative()) {
                    try (Transaction subTransaction = Transaction.open(transaction)) {
                        inserted = itemFluidHandler.insert(resourceInMachine, maxExtract, subTransaction);
                    }
                } else {
                    inserted = itemFluidHandler.insert(resourceInMachine, maxExtract, transaction);
                }

                if (inserted > 0) {
                    if (itemStack.getCount() == 1) {
                        player.setItemInHand(hand, copyStack);
                    } else if (itemStack.getCount() > 1 && player.getInventory().add(copyStack)) {
                        itemStack.shrink(1);
                    } else {
                        player.drop(copyStack, false, true);
                        itemStack.shrink(1);
                    }

                    if (player.isCreative()) {
                        ItemStack copy = itemStack.copyWithCount(1);
                        var newItemHandler = ItemAccess.forStack(copy).getCapability(Capabilities.Fluid.ITEM);
                        if (newItemHandler != null) {
                            if (inserted != newItemHandler.insert(resourceInMachine, inserted, transaction)) {
                                // Failed to insert, abort.
                                return false;
                            }
                        }

                        if (!player.getInventory().add(copy)) {
                            player.drop(copy, false, true);
                        }
                    }

                    if (inserted != fluidStorage.extract(resourceInMachine, inserted, transaction)) {
                        return false;
                    }

                    transaction.commit();
                    return true;
                }
            } else if (fluidStorage.layout().slotConfig(tankSlot).canInsert()) {
                int filled = fluidStorage.insert(tankSlot, resourceFromItem, amountAvailable, transaction);
                if (filled <= 0) {
                    return false;
                }

                // Extract from the item if not in creative.
                if (!player.isCreative()) {
                    if (filled != itemFluidHandler.extract(resourceFromItem, amountAvailable, transaction)) {
                        return false;
                    }
                }

                boolean shouldCommit = false;
                if (player.isCreative()) {
                    shouldCommit = true;
                } else if (!copyStack.isEmpty()) {
                    if (itemStack.getCount() == 1) {
                        player.setItemInHand(hand, copyStack);
                        shouldCommit = true;
                    } else if (player.getInventory().add(copyStack)) {
                        itemStack.shrink(1);
                        shouldCommit = true;
                    }
                } else {
                    itemStack.shrink(1);
                    if (itemStack.isEmpty()) {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }

                    shouldCommit = true;
                }

                if (shouldCommit) {
                    transaction.commit();
                    return true;
                }
            }
        }

        return false;
    }
}
