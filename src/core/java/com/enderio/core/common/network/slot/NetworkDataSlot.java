package com.enderio.core.common.network.slot;

import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class NetworkDataSlot<T> {

    protected Supplier<T> getter;
    private Consumer<T> setter;
    private int cachedHash;

    public NetworkDataSlot(Supplier<T> getter, Consumer<T> setter) {
        this.getter = getter;
        this.setter = setter;
    }

    @Nullable
    public final Tag serializeNBT(boolean fullUpdate) {
        T value = getter.get();
        int hash = hashCode(value);
        if (!fullUpdate && cachedHash == hash) {
            return null;
        }
        cachedHash = hash;
        return serializeValueNBT(value);
    }

    public void fromNBT(Tag nbt) {
        setter.accept(valueFromNBT(nbt));
    }

    public abstract Tag serializeValueNBT(T value);
    protected abstract T valueFromNBT(Tag nbt);

    protected int hashCode(T value) {
        return value.hashCode();
    }
}
