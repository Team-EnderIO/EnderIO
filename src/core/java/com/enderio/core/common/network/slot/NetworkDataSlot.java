package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
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
    public final Tag serializeNBT(HolderLookup.Provider lookupProvider, boolean fullUpdate) {
        T value = getter.get();
        int hash = hashCode(value);
        if (!fullUpdate && cachedHash == hash) {
            return null;
        }
        cachedHash = hash;
        return serializeValueNBT(lookupProvider, value);
    }

    public final void writeBuffer(RegistryFriendlyByteBuf buf) {
        T value = getter.get();
        cachedHash = hashCode(value);
        toBuffer(buf, value);
    }

    public void fromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        setter.accept(valueFromNBT(lookupProvider, nbt));
    }

    public void fromBuffer(RegistryFriendlyByteBuf buf) {
        setter.accept(valueFromBuffer(buf));
    }

    public abstract Tag serializeValueNBT(HolderLookup.Provider lookupProvider, T value);
    protected abstract T valueFromNBT(HolderLookup.Provider lookupProvider, Tag nbt);

    public abstract void toBuffer(RegistryFriendlyByteBuf buf, T value);

    protected abstract T valueFromBuffer(RegistryFriendlyByteBuf buf);

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

    //Called after the server is updated with the new data
    public void updateServerCallback() {

    }
}
