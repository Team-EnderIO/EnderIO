package com.enderio.conduits.common.conduit.type.item;

import com.enderio.base.api.filter.ItemStackFilter;
import com.enderio.conduits.api.ColoredRedstoneProvider;
import com.enderio.conduits.api.ConduitNetwork;
import com.enderio.conduits.api.ticker.CapabilityAwareConduitTicker;
import com.enderio.conduits.common.components.ExtractionSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitTypes;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import java.util.List;

public class ItemConduitTicker extends CapabilityAwareConduitTicker<ItemConduit, IItemHandler> {

    public static ItemConduitTicker INSTANCE = new ItemConduitTicker();

    @Override
    protected void tickCapabilityGraph(
        ServerLevel level,
        ItemConduit conduit,
        List<CapabilityConnection> inserts,
        List<CapabilityConnection> extracts,
        ConduitNetwork graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        toNextExtract:
        for (CapabilityConnection extract: extracts) {
            IItemHandler extractHandler = extract.capability();
            for (int i = 0; i < extractHandler.getSlots(); i++) {
                int speed = 4;
                if (extract.upgrade() instanceof ExtractionSpeedUpgrade speedUpgrade) {
                    speed *= (int) Math.pow(2, speedUpgrade.tier());
                }

                ItemStack extractedItem = extractHandler.extractItem(i, speed, true);
                if (extractedItem.isEmpty()) {
                    continue;
                }

                if (extract.extractFilter() instanceof ItemStackFilter itemFilter) {
                    if (!itemFilter.test(extractedItem)) {
                        continue;
                    }
                }

                ItemConduitData.ItemSidedData sidedExtractData = extract.node().getOrCreateData(ConduitTypes.Data.ITEM.get()).compute(extract.direction());
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
                        && extract.direction() == insert.direction()
                        && extract.pos() == insert.pos()) {
                        continue;
                    }

                    if (insert.insertFilter() instanceof ItemStackFilter itemFilter) {
                        if (!itemFilter.test(extractedItem)) {
                            continue;
                        }
                    }

                    ItemStack notInserted = ItemHandlerHelper.insertItem(insert.capability(), extractedItem, false);

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
    protected BlockCapability<IItemHandler, Direction> getCapability() {
        return Capabilities.ItemHandler.BLOCK;
    }

    @Override
    public int getTickRate() {
        return 20;
    }
}
