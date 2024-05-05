package com.enderio.core.common.network.slot;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.function.BiConsumer;
import java.util.function.Function;
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
        this(
            getter,
            setterCallback,
            (t, buf) -> buf.writeNbt(t.serializeNBT(buf.registryAccess())),
            buf -> {
                CompoundTag tag = buf.readNbt();
                T t = getter.get();
                t.deserializeNBT(buf.registryAccess(), tag);
                return t;
            });
    }

    public NBTSerializableNetworkDataSlot(
        Supplier<T> getter,
        Callback setterCallback,
        BiConsumer<T, RegistryFriendlyByteBuf> toBuffer,
        Function<RegistryFriendlyByteBuf, T> fromBuffer) {

        //I can put null here, because I override the only usage of the setter
        super(getter, INBTSerializable::serializeNBT, INBTSerializable::deserializeNBT, toBuffer, fromBuffer);
        this.setterCallback = setterCallback;
    }

    @Override
    public void fromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        super.fromNBT(lookupProvider, nbt);
        setterCallback.call();
    }

    @Override
    public void fromBuffer(RegistryFriendlyByteBuf buf) {
        super.fromBuffer(buf);
        setterCallback.call();
    }

    public interface Callback {
        void call();
    }
}
