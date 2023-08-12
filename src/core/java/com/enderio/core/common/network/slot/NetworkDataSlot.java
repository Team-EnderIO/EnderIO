package com.enderio.core.common.network.slot;

import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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

    public final void writeBuffer(FriendlyByteBuf buf) {
        T value = getter.get();
        cachedHash = hashCode(value);
        toBuffer(buf, value);
    }

    public void fromNBT(Tag nbt) {
        setter.accept(valueFromNBT(nbt));
    }

    public void fromBuffer(FriendlyByteBuf buf) {
        setter.accept(valueFromBuffer(buf));
    }

    public abstract Tag serializeValueNBT(T value);
    protected abstract T valueFromNBT(Tag nbt);

    public abstract void toBuffer(FriendlyByteBuf buf, T value);

    protected abstract T valueFromBuffer(FriendlyByteBuf buf);

    public boolean needsUpdate() {
        T value = getter.get();
        int hash = hashCode(value);
        if (cachedHash == hash) {
            return false;
        }
        return true;
    }

    protected int hashCode(T value) {
        return value.hashCode();
    }
}
