package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NBTSerializingDataSlot<T> extends EnderDataSlot<T> {
    private final Function<T, CompoundTag> toNBT;
    private final BiConsumer<T, CompoundTag> handleNBT;

    public NBTSerializingDataSlot(Supplier<T> getter, Function<T, CompoundTag> toNBT, BiConsumer<T, CompoundTag> handleNBT, SyncMode mode) {
        super(getter, null, mode);
        this.toNBT = toNBT;
        this.handleNBT = handleNBT;
    }

    @Override
    public CompoundTag toFullNBT() {
        return toNBT.apply(getter().get());
    }

    @Override
    protected T fromNBT(CompoundTag nbt) {
        //I can return null here, because I override the only usage of this method
        return null;
    }

    @Override
    public void handleNBT(CompoundTag tag) {
        handleNBT.accept(getter().get(), tag);
    }
}
