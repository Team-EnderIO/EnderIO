package com.enderio.conduits.common.conduit.type.item;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.filter.ItemStackFilter;
import com.enderio.conduits.common.capability.ExtractionSpeedUpgrade;
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
            int extracted = 0;

            nextItem:
            for (int i = 0; i < extractHandler.getSlots(); i++) {
                int speed = 4;
                if (extract.upgrade instanceof ExtractionSpeedUpgrade speedUpgrade) {
                    speed *= (int) Math.pow(2, speedUpgrade.tier());
                }

                ItemStack extractedItem = extractHandler.extractItem(i, speed - extracted, true);
                if (extractedItem.isEmpty()) {
                    continue;
                }

                if (extract.extractFilter instanceof ItemStackFilter itemFilter) {
                    if (!itemFilter.test(extractedItem)) {
                        continue;
                    }
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

                    if (insert.insertFilter instanceof ItemStackFilter itemFilter) {
                        if (!itemFilter.test(extractedItem)) {
                            continue;
                        }
                    }

                    ItemStack notInserted = ItemHandlerHelper.insertItem(insert.capability, extractedItem, false);
                    if (notInserted.getCount() < extractedItem.getCount()) {
                        extracted += extractedItem.getCount() - notInserted.getCount();
                        extractHandler.extractItem(i, extracted, false);
                        if (extracted >= speed) {
                            if (sidedExtractData.isRoundRobin) {
                                sidedExtractData.rotatingIndex = insertIndex + 1;
                            }
                            continue toNextExtract;
                        } else {
                            continue nextItem;
                        }
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
