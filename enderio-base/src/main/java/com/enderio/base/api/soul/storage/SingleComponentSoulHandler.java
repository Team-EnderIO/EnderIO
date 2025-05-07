package com.enderio.base.api.soul.storage;

import com.enderio.base.api.soul.Soul;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

public class SingleComponentSoulHandler implements ISoulHandlerModifiable {

    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<Soul> componentType;

    public SingleComponentSoulHandler(MutableDataComponentHolder parent, DataComponentType<Soul> componentType) {
        this.parent = parent;
        this.componentType = componentType;
    }

    @Override
    public void setSoulInSlot(int slot, Soul soul) {
        validateSlotIndex(slot);
        if (!isSoulValid(slot, soul)) {
            throw new IllegalArgumentException("Soul is not valid for this slot");
        }

        parent.set(componentType, soul);
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Override
    public Soul getSoulInSlot(int slot) {
        validateSlotIndex(slot);
        return parent.getOrDefault(componentType, Soul.EMPTY);
    }

    @Override
    public boolean tryInsertSoul(Soul soul, boolean isSimulate) {
        var currentSoul = getSoulInSlot(0);

        // Cannot bind if we already have a soul
        if (currentSoul.hasEntity()) {
            return false;
        }

        // Ensure the soul is valid
        if (!isSoulValid(0, soul)) {
            return false;
        }

        // It's a valid soul, set it (if we're not simulating)
        if (!isSimulate) {
            setSoulInSlot(0, soul);
        }
        return true;
    }

    @Override
    public Soul tryExtractSoul(boolean isSimulate) {
        var currentSoul = getSoulInSlot(0);

        // Cannot extract if we have no soul
        if (!currentSoul.hasEntity()) {
            return Soul.EMPTY;
        }

        // It's a valid soul, set it (if we're not simulating)
        if (!isSimulate) {
            setSoulInSlot(0, Soul.EMPTY);
        }
        return currentSoul.copy();
    }

    @Override
    public boolean isSoulValid(int slot, Soul soul) {
        return true;
    }

    protected final void validateSlotIndex(int slot) {
        if (slot != 0) {
            throw new IllegalArgumentException("Slot index must be 0");
        }
    }
}
