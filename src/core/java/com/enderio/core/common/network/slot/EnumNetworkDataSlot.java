package com.enderio.core.common.network.slot;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumNetworkDataSlot<T extends Enum<T>> extends NetworkDataSlot<T> {

    private final Class<T> enumClass;

    public EnumNetworkDataSlot(Class<T> enumClass, Supplier<T> getter, Consumer<T> setter) {
        super(getter, setter);
        this.enumClass = enumClass;
    }

    @Override
    public Tag serializeValueNBT(T value) {
        return IntTag.valueOf(value.ordinal());
    }

    @Override
    protected T valueFromNBT(Tag nbt) {
        if (nbt instanceof IntTag intTag) {
            return enumClass.getEnumConstants()[intTag.getAsInt()];
        } else {
            throw new IllegalStateException("Invalid int tag was passed over the network.");
        }
    }
}
