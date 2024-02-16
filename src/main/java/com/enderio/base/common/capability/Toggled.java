package com.enderio.base.common.capability;

import com.enderio.api.capability.IToggled;
import com.enderio.base.EIONBTKeys;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class Toggled implements IToggled {

    private final ItemStack stack;

    public Toggled(ItemStack stack) {
        this.stack = stack;
    }

    public static boolean isEnabled(ItemStack itemStack) {
        var toggledCap = itemStack.getCapability(EIOCapabilities.Toggled.ITEM);
        return toggledCap != null && toggledCap.isEnabled();
    }

    public static void toggleEnabled(ItemStack itemStack) {
        var toggledCap = itemStack.getCapability(EIOCapabilities.Toggled.ITEM);
        if (toggledCap != null) {
            toggledCap.toggle();
        }
    }

    public static void setEnabled(ItemStack itemStack, boolean enabled) {
        var toggledCap = itemStack.getCapability(EIOCapabilities.Toggled.ITEM);
        if (toggledCap != null) {
            toggledCap.setEnabled(enabled);
        }
    }

    @Override
    public boolean isEnabled() {
        CompoundTag tag = this.stack.getOrCreateTag();
        if (tag.contains(EIONBTKeys.TOGGLE_STATE)) {
            return tag.getBoolean(EIONBTKeys.TOGGLE_STATE);
        }

        return false;
    }

    @Override
    public void toggle() {
        setEnabled(!isEnabled());
    }

    @Override
    public void setEnabled(boolean isEnabled) {
        CompoundTag tag = this.stack.getOrCreateTag();
        tag.putBoolean(EIONBTKeys.TOGGLE_STATE, isEnabled);
    }
}
