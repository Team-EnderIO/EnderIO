package com.enderio.base.common.capacitor;

import com.enderio.EnderIO;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.ICapacitorData;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;

public enum DefaultCapacitorData implements ICapacitorData {
    NONE(0),
    BASIC(1.0f),
    DOUBLE_LAYER(2.0f),
    OCTADIC(3.0f);

    private final float base;

    DefaultCapacitorData(float base) {
        this.base = base;
    }

    @Override
    public float getBase() {
        return base;
    }

    @Override
    public float getModifier(CapacitorModifier modifier) {
        return getBase();
    }

    @Override
    public Tag serializeNBT() {
        return FloatTag.valueOf(base);
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        EnderIO.LOGGER.warn("Tried to deserialize NBT for a default capacitor datum.");
    }
}
