package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumNetworkDataSlot<T extends Enum<T>> extends NetworkDataSlot<T> {

    private final Class<T> enumClass;

    public EnumNetworkDataSlot(Class<T> enumClass, Supplier<T> getter, Consumer<T> setter) {
        super(getter, setter);
        this.enumClass = enumClass;
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider, T value) {
        return IntTag.valueOf(value.ordinal());
    }

    @Override
    protected T valueFromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        if (nbt instanceof IntTag intTag) {
            return enumClass.getEnumConstants()[intTag.getAsInt()];
        } else {
            throw new IllegalStateException("Invalid enum/int tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(RegistryFriendlyByteBuf buf, T value) {
        buf.writeInt(value.ordinal());
    }

    @Override
    public T valueFromBuffer(RegistryFriendlyByteBuf buf) {
        try {
            return enumClass.getEnumConstants()[buf.readInt()];
        } catch (Exception e) {
            throw new IllegalStateException("Invalid enum/int buffer was passed over the network.");
        }
    }
}
