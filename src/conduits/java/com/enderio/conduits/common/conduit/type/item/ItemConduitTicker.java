package com.enderio.conduits.common.conduit.type.item;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.filter.ItemStackFilter;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.components.ItemSpeedUpgrade;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;

public class ItemConduitTicker extends CapabilityAwareConduitTicker<ItemExtendedData, IItemHandler> {

    @Override
    protected void tickCapabilityGraph(
        ConduitType<ItemExtendedData> type,
        List<CapabilityConnection<ItemExtendedData, IItemHandler>> inserts,
        List<CapabilityConnection<ItemExtendedData, IItemHandler>> extracts,
        ServerLevel level,
        Graph<Mergeable.Dummy> graph,
        ColoredRedstoneProvider coloredRedstoneProvider) {

        toNextExtract:
        for (CapabilityConnection<ItemExtendedData, IItemHandler> extract: extracts) {
            IItemHandler extractHandler = extract.capability();
            for (int i = 0; i < extractHandler.getSlots(); i++) {
                int speed = 4;
                if (extract.upgrade() instanceof ItemSpeedUpgrade speedUpgrade) {
                    speed *= speedUpgrade.getSpeed();
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

                ItemExtendedData.ItemSidedData sidedExtractData = extract.data().compute(extract.direction());
                if (sidedExtractData.isRoundRobin) {
                    if (inserts.size() <= sidedExtractData.rotatingIndex) {
                        sidedExtractData.rotatingIndex = 0;
                    }
                } else {
                    sidedExtractData.rotatingIndex = 0;
                }

                for (int j = sidedExtractData.rotatingIndex; j < sidedExtractData.rotatingIndex + inserts.size(); j++) {
                    int insertIndex = j % inserts.size();
                    CapabilityConnection<ItemExtendedData, IItemHandler> insert = inserts.get(insertIndex);

                    if (!sidedExtractData.isSelfFeed
                        && extract.direction() == insert.direction()
                        && extract.data() == insert.data()) {
                        continue;
                    }

                    if (extract.insertFilter() instanceof ItemStackFilter itemFilter) {
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
