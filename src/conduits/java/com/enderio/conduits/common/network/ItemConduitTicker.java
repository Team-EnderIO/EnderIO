package com.enderio.conduits.common.network;

import com.enderio.api.conduit.ticker.ICapabilityAwareConduitTicker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class ItemConduitTicker implements ICapabilityAwareConduitTicker<IItemHandler> {

    @Override
    public void tickCapabilityGraph(List<IItemHandler> inserts, List<IItemHandler> extracts, ServerLevel level) {
        toNextExtract:
        for (IItemHandler extract : extracts) {
            for (int i = 0; i < extract.getSlots(); i++) {
                ItemStack extractedItem = extract.extractItem(i, 4, true);
                if (extractedItem.isEmpty())
                    continue;
                for (IItemHandler insert : inserts) {
                    if (insert == extract)
                        continue;
                    ItemStack notInserted = ItemHandlerHelper.insertItem(insert, extractedItem, false);
                    if (notInserted.getCount() < extractedItem.getCount()) {
                        extract.extractItem(i, extractedItem.getCount() - notInserted.getCount(), false);
                        continue toNextExtract;
                    }
                }
            }
        }
    }

    @Override
    public Capability<IItemHandler> getCapability() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }
}
