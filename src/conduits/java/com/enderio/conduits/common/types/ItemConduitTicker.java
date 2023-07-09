package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class ItemConduitTicker extends CapabilityAwareConduitTicker<IItemHandler> {

    @Override
    protected void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts, ServerLevel level, Graph<Mergeable.Dummy> graph) {
        toNextExtract:
        for (CapabilityConnection extract: extracts) {
            IItemHandler extractHandler = extract.cap;
            for (int i = 0; i < extractHandler.getSlots(); i++) {
                ItemStack extractedItem = extractHandler.extractItem(i, 4, true);
                if (extractedItem.isEmpty())
                    continue;
                ItemExtendedData.ItemSidedData sidedExtractData = extract.data.castTo(ItemExtendedData.class).compute(extract.direction);
                if (sidedExtractData.roundRobin) {
                    if (inserts.size() <= sidedExtractData.rotatingIndex) {
                        sidedExtractData.rotatingIndex = 0;
                    }
                    for (int j = 0; j < sidedExtractData.rotatingIndex; j++) {
                        //empty lists are verified in ICapabilityAwareConduitTicker
                        //this moves the first element to the back to give a new cap the next time this is called
                        inserts.add(inserts.remove(0));
                    }
                }
                for (int j = 0; j < inserts.size(); j++) {
                    CapabilityConnection insert = inserts.get(j);
                    if (!sidedExtractData.selfFeed
                        && extract.direction == insert.direction
                        && extract.data == insert.data)
                        continue;
                    ItemStack notInserted = ItemHandlerHelper.insertItem(insert.cap, extractedItem, false);
                    if (notInserted.getCount() < extractedItem.getCount()) {
                        extractHandler.extractItem(i, extractedItem.getCount() - notInserted.getCount(), false);
                        if (sidedExtractData.roundRobin) {
                            sidedExtractData.rotatingIndex += j + 1;
                        }
                        continue toNextExtract;
                    }
                }
            }
        }
    }

    @Override
    protected Capability<IItemHandler> getCapability() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    @Override
    public int getTickRate() {
        return 20;
    }
}
