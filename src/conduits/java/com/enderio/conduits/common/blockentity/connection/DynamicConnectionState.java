package com.enderio.conduits.common.blockentity.connection;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.blockentity.SlotType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;

public record DynamicConnectionState(boolean isInsert, ColorControl insert, boolean isExtract, ColorControl extract, RedstoneControl control, ColorControl redstoneChannel, @UseOnly(LogicalSide.SERVER) ItemStack filterInsert, @UseOnly(LogicalSide.SERVER) ItemStack filterExtract, @UseOnly(LogicalSide.SERVER) ItemStack upgradeExtract) implements IConnectionState {

    public static DynamicConnectionState defaultConnection(IConduitType<?> type) {
        IConduitType.ConduitConnectionData defaultConnection = type.getDefaultConnection();
        return new DynamicConnectionState(defaultConnection.isInsert(), ColorControl.GREEN, defaultConnection.isExtract(), ColorControl.GREEN, RedstoneControl.NEVER_ACTIVE, ColorControl.RED, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
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

    public DynamicConnectionState withColor(boolean forExtract, ColorControl value) {
        return new DynamicConnectionState(isInsert, !forExtract ? value : insert, isExtract, forExtract ? value : extract, control, redstoneChannel, filterInsert, filterExtract, upgradeExtract);
    }
    public DynamicConnectionState withRedstoneMode(RedstoneControl value) {
        return new DynamicConnectionState(isInsert, insert, isExtract, extract, value, redstoneChannel, filterInsert, filterExtract, upgradeExtract);
    }
    public DynamicConnectionState withRedstoneChannel(ColorControl value) {
        return new DynamicConnectionState(isInsert, insert, isExtract, extract, control, value, filterInsert, filterExtract, upgradeExtract);
    }

    public boolean isEmpty() {
        return !isInsert && !isExtract;
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeBoolean(isInsert);
        buf.writeEnum(insert);
        buf.writeBoolean(isExtract);
        buf.writeEnum(extract);
        buf.writeEnum(control);
        buf.writeEnum(redstoneChannel);
        buf.writeItemStack(filterInsert, false);
        buf.writeItemStack(filterExtract, false);
        buf.writeItemStack(upgradeExtract, false);
    }

    public static DynamicConnectionState fromNetwork(FriendlyByteBuf buf) {
        boolean isInsert = buf.readBoolean();
        ColorControl insert = buf.readEnum(ColorControl.class);
        boolean isExtract = buf.readBoolean();
        ColorControl extract = buf.readEnum(ColorControl.class);
        RedstoneControl control = buf.readEnum(RedstoneControl.class);
        ColorControl redstoneChannel = buf.readEnum(ColorControl.class);
        ItemStack filterInsert = buf.readItem();
        ItemStack filterExtract = buf.readItem();
        ItemStack upgradeInsert = buf.readItem();
        return new DynamicConnectionState(isInsert, insert, isExtract, extract, control, redstoneChannel, filterInsert, filterExtract, upgradeInsert);
    }
}
