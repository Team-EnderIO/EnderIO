package com.enderio.enderio.foundation.fluid;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class FluidInteractionHelper {
    public static boolean handleFluidItemInteraction(ItemAccess itemAccess, ResourceStorage<FluidResource> fluidStorage,
        SingleResourceSlotKey<FluidResource> tankSlot) {

        var itemFluidHandler = itemAccess.oneByOne().getCapability(Capabilities.Fluid.ITEM);
        if (itemFluidHandler == null) {
            return false;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            FluidResource resourceFromItem = fluidStorage.getResource(tankSlot);
            int amountAvailable = 0;
            try (Transaction subTransaction = Transaction.open(transaction)) {
                if (!resourceFromItem.isEmpty()) {
                    amountAvailable = itemFluidHandler.extract(resourceFromItem, Integer.MAX_VALUE, subTransaction);
                }

                if (amountAvailable <= 0) {
                    // If we have no storage, see what's in the item.
                    ResourceStack<FluidResource> resourceInItem = ResourceHandlerUtil.extractFirst(itemFluidHandler, fr -> true, Integer.MAX_VALUE,
                        subTransaction);
                    if (resourceInItem != null) {
                        resourceFromItem = resourceInItem.resource();
                        amountAvailable = resourceInItem.amount();
                    }
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
                int inserted = itemFluidHandler.insert(resourceInMachine, maxExtract, transaction);

                // Unable to insert any fluid
                if (inserted <= 0) {
                    return false;
                }

                if (inserted != fluidStorage.extract(resourceInMachine, inserted, transaction)) {
                    return false;
                }

                transaction.commit();
                return true;
            } else if (fluidStorage.layout().slotConfig(tankSlot).externalRules().canInsert(resourceFromItem)) {
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
