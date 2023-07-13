package com.enderio.core.common.network.slot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Supplier;

public class NBTSerializableNetworkDataSlot<T extends INBTSerializable<CompoundTag>> extends NBTSerializingNetworkDataSlot<T> {
    /**
     * You can add a callback here, for a ModelData Reload for example, because a setter will never be called
     */
    private final Callback setterCallback;

    public NBTSerializableNetworkDataSlot(Supplier<T> getter) {
        this(getter, () -> {});
    }

    public NBTSerializableNetworkDataSlot(Supplier<T> getter, Callback setterCallback) {
        //I can put null here, because I override the only usage of the setter
        super(getter, INBTSerializable::serializeNBT, INBTSerializable::deserializeNBT);
        this.setterCallback = setterCallback;
    }

    @Override
    public void fromNBT(Tag nbt) {
        super.fromNBT(nbt);
        setterCallback.call();
    }

    public interface Callback {
        void call();
    }
}
