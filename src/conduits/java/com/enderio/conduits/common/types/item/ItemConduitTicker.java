package com.enderio.conduits.common.types.item;

import com.enderio.api.capability.IConduitUpgrade;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.components.ItemSpeedUpgrade;
import com.enderio.conduits.common.init.ConduitCapabilities;
import com.enderio.core.common.capability.IFilterCapability;
import com.enderio.core.common.capability.ItemFilterCapability;
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

public class ItemConduitTicker extends CapabilityAwareConduitTicker<IItemHandler> {

    @Override
    protected void tickCapabilityGraph(ConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        toNextExtract:
        for (CapabilityConnection extract: extracts) {
            IItemHandler extractHandler = extract.cap;
            for (int i = 0; i < extractHandler.getSlots(); i++) {
                int speed = 4;
                if (extract.connectionState != null) {
                    ItemStack upgradeStack = extract.connectionState.upgradeExtract();
                    IConduitUpgrade upgrade = upgradeStack.getCapability(ConduitCapabilities.ConduitUpgrade.ITEM);
                    if (upgrade instanceof ItemSpeedUpgrade speedUpgrade) {
                        speed *= speedUpgrade.getSpeed();
                    }
                }
                ItemStack extractedItem = extractHandler.extractItem(i, speed, true);
                if (extractedItem.isEmpty()) {
                    continue;
                }

                if (extract.connectionState != null && !extract.connectionState.filterExtract().isEmpty()) {
                    ItemStack stack = extract.connectionState.filterExtract();
                    IFilterCapability capability = stack.getCapability(EIOCapabilities.Filter.ITEM);
                    if (capability instanceof ItemFilterCapability cap && !cap.test(extractedItem)) {
                        continue;
                    }
                }

                ItemExtendedData.ItemSidedData sidedExtractData = extract.data.castTo(ItemExtendedData.class).compute(extract.direction);
                if (sidedExtractData.roundRobin) {
                    if (inserts.size() <= sidedExtractData.rotatingIndex) {
                        sidedExtractData.rotatingIndex = 0;
                    }
                } else {
                    sidedExtractData.rotatingIndex = 0;
                }

                for (int j = sidedExtractData.rotatingIndex; j < sidedExtractData.rotatingIndex + inserts.size(); j++) {
                    int insertIndex = j % inserts.size();
                    CapabilityConnection insert = inserts.get(insertIndex);

                    if (!sidedExtractData.selfFeed
                        && extract.direction == insert.direction
                        && extract.data == insert.data) {
                        continue;
                    }

                    if (insert.connectionState != null && !insert.connectionState.filterInsert().isEmpty()) {
                        ItemStack stack = insert.connectionState.filterInsert();
                        IFilterCapability capability = stack.getCapability(EIOCapabilities.Filter.ITEM);
                        if (capability instanceof ItemFilterCapability cap && !cap.test(extractedItem)) {
                            continue;
                        }
                    }

                    ItemStack notInserted = ItemHandlerHelper.insertItem(insert.cap, extractedItem, false);

                    if (notInserted.getCount() < extractedItem.getCount()) {
                        extractHandler.extractItem(i, extractedItem.getCount() - notInserted.getCount(), false);
                        if (sidedExtractData.roundRobin) {
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
