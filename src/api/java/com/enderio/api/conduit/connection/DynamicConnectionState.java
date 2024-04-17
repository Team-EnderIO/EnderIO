package com.enderio.api.conduit.connection;

import com.enderio.api.UseOnly;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.blockentity.SlotType;
import com.enderio.core.common.network.MassiveStreamCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;

import java.util.HashMap;
import java.util.Map;

public record DynamicConnectionState(
    boolean isInsert,
    ColorControl insert,
    boolean isExtract,
    ColorControl extract,
    RedstoneControl control,
    ColorControl redstoneChannel,
    @UseOnly(LogicalSide.SERVER) ItemStack filterInsert,
    @UseOnly(LogicalSide.SERVER) ItemStack filterExtract,
    @UseOnly(LogicalSide.SERVER) ItemStack upgradeExtract
) implements ConnectionState {

    public static StreamCodec<RegistryFriendlyByteBuf, DynamicConnectionState> STREAM_CODEC = MassiveStreamCodec.composite(
        ByteBufCodecs.BOOL,
        DynamicConnectionState::isInsert,
        ColorControl.STREAM_CODEC,
        DynamicConnectionState::insert,
        ByteBufCodecs.BOOL,
        DynamicConnectionState::isExtract,
        ColorControl.STREAM_CODEC,
        DynamicConnectionState::extract,
        RedstoneControl.STREAM_CODEC,
        DynamicConnectionState::control,
        ColorControl.STREAM_CODEC,
        DynamicConnectionState::redstoneChannel,
        ItemStack.OPTIONAL_STREAM_CODEC,
        DynamicConnectionState::filterInsert,
        ItemStack.OPTIONAL_STREAM_CODEC,
        DynamicConnectionState::filterExtract,
        ItemStack.OPTIONAL_STREAM_CODEC,
        DynamicConnectionState::upgradeExtract,
        DynamicConnectionState::new
    );

    public static DynamicConnectionState defaultConnection(Level level, BlockPos pos, Direction direction, ConduitType<?> type) {
        ConduitType.ConduitConnectionData defaultConnection = type.getDefaultConnection(level, pos, direction);
        return new DynamicConnectionState(defaultConnection.isInsert(), ColorControl.GREEN, defaultConnection.isExtract(), ColorControl.GREEN, defaultConnection.control(), ColorControl.RED, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
    }

    @Override
    public boolean isConnection() {
        return true;
    }

    public ItemStack getItem(SlotType slotType) {
        if (slotType == SlotType.FILTER_EXTRACT) {
            return filterExtract;
        }

        if (slotType == SlotType.FILTER_INSERT) {
            return filterInsert;
        }

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
}
