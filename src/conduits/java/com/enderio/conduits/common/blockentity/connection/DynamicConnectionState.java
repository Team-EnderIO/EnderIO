package com.enderio.conduits.common.blockentity.connection;

import com.enderio.api.UseOnly;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.conduits.common.blockentity.SlotType;
import com.enderio.core.common.blockentity.ColorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;

public record DynamicConnectionState(boolean isInsert, ColorControl insert, boolean isExtract, ColorControl extract, RedstoneControl control, ColorControl redstoneChannel, @UseOnly(LogicalSide.SERVER) ItemStack filterInsert, @UseOnly(LogicalSide.SERVER) ItemStack filterExtract, @UseOnly(LogicalSide.SERVER) ItemStack upgradeExtract) implements IConnectionState {

    public static DynamicConnectionState defaultConnection() {
        return new DynamicConnectionState(false, ColorControl.GREEN, true, ColorControl.GREEN, RedstoneControl.NEVER_ACTIVE, ColorControl.RED, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean isConnection() {
        return true;
    }

    public ItemStack getItem(SlotType slotType) {
        if (slotType == SlotType.FILTER_EXTRACT)
            return filterExtract;
        if (slotType == SlotType.FILTER_INSERT)
            return filterInsert;
        return upgradeExtract;
    }

    public DynamicConnectionState withItem(SlotType type, ItemStack stack) {
        Map<SlotType, ItemStack> items = new HashMap<>();
        for (SlotType type1: SlotType.values()) {
            items.put(type1, type1 == type ? stack: getItem(type1));
        }
        return new DynamicConnectionState(isInsert, insert, isExtract, extract, control, redstoneChannel, items.get(SlotType.FILTER_INSERT), items.get(SlotType.FILTER_EXTRACT), items.get(SlotType.UPGRADE_EXTRACT));
    }
    public DynamicConnectionState withEnabled(boolean forExtract, boolean value) {
        return new DynamicConnectionState(!forExtract ? value : isInsert, insert, forExtract ? value : isExtract, extract, control, redstoneChannel, filterInsert, filterExtract, upgradeExtract);
    }
}
