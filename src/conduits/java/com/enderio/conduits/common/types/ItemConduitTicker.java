package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOItems;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.blockentity.SlotData;
import com.enderio.conduits.common.blockentity.SlotType;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.function.TriFunction;

import java.util.List;


public class ItemConduitTicker extends CapabilityAwareConduitTicker<IItemHandler> {

    // TODO move this somewhere safe
    public static class FilterUtils {
        private static final String KEY_ITEM_FILTER = "ItemFilter";
        private static final String KEY_IGNORE_MODE = "IgnoreMode"; // make selected items be ignored by the conduit
        private static final String KEY_STRICT_MODE = "StrictMode"; // means that stack counts mater

        /*
         Itemfilter format
         tag: {ItemFilter: [{Count: 1L, id: "ID"}, {Count: 1L, tag: "forge:ingots"}], IgnoreMode: false, StrictMode: false}
         */

        public static boolean isAllowed(ItemStack filterItem, ItemStack item) {
            if (filterItem.isEmpty()) return true;
            if (item.isEmpty()) return false;

            var itemTag = filterItem.getOrCreateTag();
            if (!itemTag.contains(KEY_ITEM_FILTER)) {
                itemTag.put(KEY_ITEM_FILTER, new ListTag());
            }

            var itemFilter = itemTag.getList(KEY_ITEM_FILTER, 10);
            var isInverted = itemTag.getBoolean(KEY_IGNORE_MODE);
            var isStrict = itemTag.getBoolean(KEY_STRICT_MODE);

            // this is a generic filter
            if (filterItem.is(EIOItems.BASIC_ITEM_FILTER.asItem()) || filterItem.is(EIOItems.BIG_ITEM_FILTER.asItem())) {
                for (int i = 0; i < itemFilter.size(); i++) {
                    var itemFilterPredicate = ItemStack.of(itemFilter.getCompound(i));
                    if (isStrict ? itemFilterPredicate == item : itemFilterPredicate.is(item.getItem())) {
                        return !isInverted;
                    }
                }
            }

            // process fancy filters here like nbt strict/non-strict etc...
            return isInverted;
        }
    }

    @Override
    public void tickGraph(IConduitType<?> type, List<NodeIdentifier<?>> loadedNodes, ServerLevel level, Graph<Mergeable.Dummy> graph,
        TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        super.tickGraph(type, loadedNodes, level, graph, isRedstoneActive);

        for (var node : loadedNodes) {
            var blockEntity = level.getBlockEntity(node.getPos());
            if (!(blockEntity instanceof ConduitBlockEntity conduitBlockEntity)) continue;
            var conduitItemHandler = conduitBlockEntity.getConduitItemHandler();

            for (var slotType: SlotType.values()) {
                if (slotType == SlotType.UPGRADE_EXTRACT) continue; // not implemented
                for (var dir: Direction.values()) {
                    var extendedData = node.getExtendedConduitData().castTo(ItemExtendedData.class).compute(dir);
                    var item = conduitItemHandler.getStackInSlot(new SlotData(dir, conduitBlockEntity.getBundle().getTypes().indexOf(type), slotType).slotIndex());

                    if (slotType == SlotType.FILTER_EXTRACT) {
                        extendedData.extractFilter = item;
                    }
                    if (slotType == SlotType.FILTER_INSERT) {
                        extendedData.insertFilter = item;
                    }
                }
            }
        }
    }

    @Override
    protected void tickCapabilityGraph(IConduitType<?> type, List<CapabilityConnection> inserts, List<CapabilityConnection> extracts, ServerLevel level, Graph<Mergeable.Dummy> graph, TriFunction<ServerLevel, BlockPos, ColorControl, Boolean> isRedstoneActive) {
        toNextExtract:
        for (CapabilityConnection extract : extracts) {
            IItemHandler extractHandler = extract.cap;
            for (int i = 0; i < extractHandler.getSlots(); i++) {
                ItemStack extractedItem = extractHandler.extractItem(i, 4, true);
                if (extractedItem.isEmpty()) {
                    continue;
                }

                ItemExtendedData.ItemSidedData sidedExtractData = extract.data.castTo(ItemExtendedData.class).compute(extract.direction);

                if (!FilterUtils.isAllowed(sidedExtractData.extractFilter, extractedItem))
                    continue;

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

                    ItemStack notInserted = ItemHandlerHelper.insertItem(insert.cap, extractedItem, false);
                    if (notInserted.getCount() < extractedItem.getCount()) {
                        extractHandler.extractItem(i, extractedItem.getCount() - notInserted.getCount(), false);
                        if (sidedExtractData.roundRobin) {
                            sidedExtractData.rotatingIndex += insertIndex + 1;
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
