package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Supplier;

public class NBTSerializableDataSlot<T extends INBTSerializable<CompoundTag>> extends NBTSerializingDataSlot<T> {

    /**
     * You can add a callback here, for a ModelData Reload for example, because a setter will never be called
     */
    private final Callback setterCallback;

    public NBTSerializableDataSlot(Supplier<T> getter, SyncMode syncMode) {
        this(getter, syncMode, () -> {});
    }

    public NBTSerializableDataSlot(Supplier<T> getter, SyncMode syncMode, Callback setterCallback) {
        //I can put null here, because I override the only usage of the setter
        super(getter, INBTSerializable::serializeNBT, INBTSerializable::deserializeNBT, syncMode);
        this.setterCallback = setterCallback;
    }


    @Override
    public void handleNBT(CompoundTag tag) {
        super.handleNBT(tag);
        setterCallback.call();
    }

    public interface Callback {
        void call();
    }
}
