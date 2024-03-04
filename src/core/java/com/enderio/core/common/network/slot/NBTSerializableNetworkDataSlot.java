package com.enderio.core.common.network.slot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
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
        this(getter, setterCallback, (t, friendlyByteBuf) -> friendlyByteBuf.writeNbt(t.serializeNBT()), friendlyByteBuf -> {
            CompoundTag tag = friendlyByteBuf.readNbt();
            T t = getter.get();
            t.deserializeNBT(tag);
            return t;
        });
    }

    public NBTSerializableNetworkDataSlot(Supplier<T> getter, Callback setterCallback, BiConsumer<T, FriendlyByteBuf> toBuffer, Function<FriendlyByteBuf, T> fromBuffer) {
        //I can put null here, because I override the only usage of the setter
        super(getter, INBTSerializable::serializeNBT, INBTSerializable::deserializeNBT, toBuffer, fromBuffer);
        this.setterCallback = setterCallback;
    }

    @Override
    public void fromNBT(Tag nbt) {
        super.fromNBT(nbt);
        setterCallback.call();
    }

    @Override
    public void fromBuffer(FriendlyByteBuf buf) {
        super.fromBuffer(buf);
        setterCallback.call();
    }

    public interface Callback {
        void call();
    }
}
