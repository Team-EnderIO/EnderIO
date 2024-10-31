package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class RedstoneTLatchFilter implements RedstoneInsertFilter {

    private static final String KEY_ACTIVE = "Active";
    private static final String KEY_DEACTIVATED = "Deactivated";

    private final ItemStack stack;

    public RedstoneTLatchFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getOutputSignal(RedstoneConduitData data, ColorControl control) {
        boolean output = isActive();
        if (data.isActive(control) && isDeactivated()) {
            output = !output;
            setState(output, false);
        }
        if (!data.isActive(control) && !isDeactivated()) {
            setState(output, true);
        }
        return output ? 15 : 0;
    }

    public boolean isActive() {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.contains(KEY_ACTIVE) && tag.getBoolean(KEY_ACTIVE);
    }

    public boolean isDeactivated() {
        CompoundTag tag = stack.getOrCreateTag();
        return !tag.contains(KEY_DEACTIVATED) || tag.getBoolean(KEY_DEACTIVATED);
    }

    public void setState(boolean active, boolean deactivated) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(KEY_ACTIVE, active);
        tag.putBoolean(KEY_DEACTIVATED, deactivated);
    }
}
