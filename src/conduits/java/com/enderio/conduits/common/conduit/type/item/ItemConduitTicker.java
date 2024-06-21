package com.enderio.conduits.common.conduit.type.item;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class ItemConduitTicker extends CapabilityAwareConduitTicker<ItemConduitData, IItemHandler> {

    public static ItemConduitTicker INSTANCE = new ItemConduitTicker();

    @Override
    protected void tickCapabilityGraph(
        ServerLevel level,
        ConduitType<ItemConduitData> type,
        List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts,
        ConduitGraph<ItemConduitData> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        toNextExtract:
        for (CapabilityConnection extract: extracts) {
            IItemHandler extractHandler = extract.capability;
            for (int i = 0; i < extractHandler.getSlots(); i++) {
                ItemStack extractedItem = extractHandler.extractItem(i, 4, true);
                if (extractedItem.isEmpty()) {
                    continue;
                }

                ItemConduitData.ItemSidedData sidedExtractData = extract.data.castTo(ItemConduitData.class).compute(extract.direction);
                if (sidedExtractData.isRoundRobin) {
                    if (inserts.size() <= sidedExtractData.rotatingIndex) {
                        sidedExtractData.rotatingIndex = 0;
                    }
                } else {
                    sidedExtractData.rotatingIndex = 0;
                }

                for (int j = sidedExtractData.rotatingIndex; j < sidedExtractData.rotatingIndex + inserts.size(); j++) {
                    int insertIndex = j % inserts.size();
                    CapabilityConnection insert = inserts.get(insertIndex);

                    if (!sidedExtractData.isSelfFeed
                        && extract.direction == insert.direction
                        && extract.data == insert.data) {
                        continue;
                    }

                    ItemStack notInserted = ItemHandlerHelper.insertItem(insert.capability, extractedItem, false);
                    if (notInserted.getCount() < extractedItem.getCount()) {
                        extractHandler.extractItem(i, extractedItem.getCount() - notInserted.getCount(), false);
                        if (sidedExtractData.isRoundRobin) {
                            sidedExtractData.rotatingIndex = insertIndex + 1;
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
