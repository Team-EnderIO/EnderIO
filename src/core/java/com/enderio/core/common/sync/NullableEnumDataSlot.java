package com.enderio.core.common.sync;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class NullableEnumDataSlot<T extends Enum<T>> extends EnderDataSlot<T> {

    private final Class<T> clazz;
    public NullableEnumDataSlot(Supplier<T> getter, Consumer<T> setter, Class<T> clazz,SyncMode mode) {
        super(getter, setter, mode);
        this.clazz = clazz;
    }

    @Override
    public CompoundTag toFullNBT() {
        CompoundTag tag = new CompoundTag();
        T t = getter().get();
        tag.putInt("ordinal", t != null ? t.ordinal() : -1);
        return tag;
    }

    @Override
    @Nullable
    protected T fromNBT(CompoundTag nbt) {
        int ordinal = nbt.getInt("ordinal");
        return ordinal != -1 ? clazz.getEnumConstants()[ordinal] : null;
    }
}
