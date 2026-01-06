package com.enderio.enderio.foundation.io.fluid;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;

/**
 * An interface that block entities may implement in order to interact with items support IFluidHandlerItem
 * Adapted from Mekanism's FluidUtils.
 */
public interface FluidItemInteractive {

    // TODO: 1.21.11: This now works, but I want tests to cover it - and perhaps make this a utility rather than an interface.
    default boolean handleFluidItemInteraction(Player player, InteractionHand hand, ItemStack itemStack, ResourceStorage<FluidResource> fluidStorage,
        SingleResourceSlotKey<FluidResource> tankSlot) {

        var itemFluidHandler = ItemAccess.forPlayerInteraction(player, hand).getCapability(Capabilities.Fluid.ITEM);
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
                    maxExtract = fluidStorage.extract(resourceInMachine, Integer.MAX_VALUE, subTransaction);
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

                if (inserted != fluidStorage.extract(resourceInMachine, inserted, transaction)) {
                    return false;
                }

                transaction.commit();
                return true;
            } else if (fluidStorage.layout().slotConfig(tankSlot).canInsert()) {
                int filled = fluidStorage.insert(tankSlot, resourceFromItem, amountAvailable, transaction);
                if (filled <= 0) {
                    return false;
                }

                // Extract from the item
                if (filled == itemFluidHandler.extract(resourceFromItem, filled, transaction)) {
                    transaction.commit();
                    return true;
                }
            }
        }

        return false;
    }
}
