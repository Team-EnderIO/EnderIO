package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.network.CountFilterPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class RedstoneCountFilter implements RedstoneInsertFilter {

    private static final String CHANNEL_KEY = "Channel";
    private static final String MAX_COUNT_KEY = "MaxCount";
    private static final String TICKS_KEY = "Ticks";
    private static final String DEACTIVATED_KEY = "Deactivated";

    private final ItemStack stack;

    public RedstoneCountFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getOutputSignal(RedstoneConduitData data, ColorControl control) {
        ColorControl channel = getChannel();
        int maxCount = getMaxCount();
        boolean deactivated = isDeactivated();
        int count = getCount();
        if (data.isActive(channel) && deactivated) {
            count++;
            deactivated = false;
        }
        if (!data.isActive(channel)) {
            deactivated = true;
        }
        if (count > maxCount) {
            count = 1;
        }
        setCount(count);
        setDeactivated(deactivated);
        return count == maxCount ? 15 : 0;
    }

    public ColorControl getChannel() {
        CompoundTag tag = this.stack.getOrCreateTag();
        return tag.contains(CHANNEL_KEY, CompoundTag.TAG_STRING)
            ? ColorControl.valueOf(tag.getString(CHANNEL_KEY))
            : ColorControl.GREEN;
    }

    public int getMaxCount() {
        CompoundTag tag = this.stack.getOrCreateTag();
        return tag.contains(MAX_COUNT_KEY, CompoundTag.TAG_INT)
            ? tag.getInt(MAX_COUNT_KEY)
            : 8;
    }

    public int getCount() {
        CompoundTag tag = this.stack.getOrCreateTag();
        return tag.contains(TICKS_KEY, CompoundTag.TAG_INT)
            ? tag.getInt(TICKS_KEY)
            : 8;
    }

    public void setCount(int count) {
        CompoundTag tag = this.stack.getOrCreateTag();
        tag.putInt(TICKS_KEY, count);
    }

    public boolean isDeactivated() {
        CompoundTag tag = this.stack.getOrCreateTag();
        return tag.contains(DEACTIVATED_KEY, CompoundTag.TAG_BYTE)
            && tag.getBoolean(DEACTIVATED_KEY);
    }

    public void setDeactivated(boolean lastActive) {
        CompoundTag tag = this.stack.getOrCreateTag();
        tag.putBoolean(DEACTIVATED_KEY, lastActive);
    }

    public void setState(CountFilterPacket packet) {
        CompoundTag tag = this.stack.getOrCreateTag();
        tag.putString(CHANNEL_KEY, packet.channel().name());
        tag.putInt(MAX_COUNT_KEY, packet.maxCount());
        tag.putInt(TICKS_KEY, packet.count());
        tag.putBoolean(DEACTIVATED_KEY, packet.active());
    }

    public void setChannel(ColorControl channel) {
        CompoundTag tag = this.stack.getOrCreateTag();
        tag.putString(CHANNEL_KEY, channel.name());
    }
}
