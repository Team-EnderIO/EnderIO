package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class DoubleRedstoneChannel {

    private static final String CHANNEL1_KEY = "Channel1";
    private static final String CHANNEL2_KEY = "Channel2";

    private final ItemStack stack;

    public DoubleRedstoneChannel(ItemStack stack) {
        this.stack = stack;
    }

    public ColorControl getFirstChannel() {
        CompoundTag tag = this.stack.getOrCreateTag();
        return tag.contains(CHANNEL1_KEY, CompoundTag.TAG_STRING)
            ? ColorControl.valueOf(tag.getString(CHANNEL1_KEY))
            : ColorControl.GREEN;
    }

    public ColorControl getSecondChannel() {
        CompoundTag tag = this.stack.getOrCreateTag();
        return tag.contains(CHANNEL2_KEY, CompoundTag.TAG_STRING)
            ? ColorControl.valueOf(tag.getString(CHANNEL2_KEY))
            : ColorControl.BROWN;
    }

    public void setFirstChannel(ColorControl channel1) {
        CompoundTag tag = this.stack.getOrCreateTag();
        tag.putString(CHANNEL1_KEY, channel1.name());
    }

    public void setSecondChannel(ColorControl channel2) {
        CompoundTag tag = this.stack.getOrCreateTag();
        tag.putString(CHANNEL2_KEY, channel2.name());
    }

    public void setChannels(ColorControl channel1, ColorControl channel2) {
        CompoundTag tag = this.stack.getOrCreateTag();
        tag.putString(CHANNEL1_KEY, channel1.name());
        tag.putString(CHANNEL2_KEY, channel2.name());
    }
}
