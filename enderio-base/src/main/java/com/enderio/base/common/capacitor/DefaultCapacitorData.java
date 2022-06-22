package com.enderio.base.common.capacitor;

import com.enderio.api.capability.ICapacitorData;
import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.EnderIO;
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
    public float getLevel(CapacitorKey key) {
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
