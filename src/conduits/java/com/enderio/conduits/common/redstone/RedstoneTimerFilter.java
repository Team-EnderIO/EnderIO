package com.enderio.conduits.common.redstone;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RedstoneTimerFilter implements RedstoneExtractFilter {

    private static final String KEY_TICKS = "Ticks";
    private static final String KEY_MAX_TICKS = "MaxTicks";

    private final ItemStack stack;

    public RedstoneTimerFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getInputSignal(Level level, BlockPos pos, Direction direction) {
        int ticks = getTicks();
        ticks += 2; //TODO Conduits tick every 2 ticks, so make this clear in the gui
        int maxTicks = getMaxTicks();
        if (ticks >= maxTicks) {
            ticks = 0;
            setTimer(ticks, maxTicks);
            return 15;
        }
        setTimer(ticks, maxTicks);
        return 0;
    }

    public int getTicks() {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.contains(KEY_TICKS) ? tag.getInt(KEY_TICKS) : 0;
    }

    public int getMaxTicks() {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.contains(KEY_MAX_TICKS) ? tag.getInt(KEY_MAX_TICKS) : 20;
    }

    public void setTimer(int ticks, int maxTicks) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(KEY_TICKS, ticks);
        tag.putInt(KEY_MAX_TICKS, maxTicks);
    }

    public void setMaxTicks(int maxTicks) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(KEY_MAX_TICKS, maxTicks);
    }
}
