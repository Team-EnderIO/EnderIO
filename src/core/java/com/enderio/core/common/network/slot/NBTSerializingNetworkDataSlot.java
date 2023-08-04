package com.enderio.core.common.network.slot;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NBTSerializingNetworkDataSlot<T> extends NetworkDataSlot<T> {
    private final Function<T, CompoundTag> toNBT;
    private final BiConsumer<T, CompoundTag> handleNBT;
    private final BiConsumer<T, FriendlyByteBuf> toBuffer;
    private final Function<FriendlyByteBuf, T> fromBuffer;

    public NBTSerializingNetworkDataSlot(Supplier<T> getter, Function<T, CompoundTag> toNBT, BiConsumer<T, CompoundTag> handleNBT, BiConsumer<T, FriendlyByteBuf> toBuffer, Function<FriendlyByteBuf, T> fromBuffer) {
        super(getter, null);
        this.toNBT = toNBT;
        this.handleNBT = handleNBT;
        this.toBuffer = toBuffer;
        this.fromBuffer = fromBuffer;
    }

    @Override
    public Tag serializeValueNBT(T value) {
        return toNBT.apply(value);
    }

    // We can return null here because we override this method's usage
    @Override
    protected T valueFromNBT(Tag nbt) {
        return null;
    }

    @Override
    public void fromNBT(Tag nbt) {
        if (nbt instanceof CompoundTag compoundTag) {
            handleNBT.accept(getter.get(), compoundTag);
        } else {
            throw new IllegalStateException("Invalid compound tag was passed over the network.");
        }
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, T value) {
        toBuffer.accept(value, buf);
    }

    @Override
    public T valueFromBuffer(FriendlyByteBuf buf) {
        try {
            return fromBuffer.apply(buf);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid compound tag buffer was passed over the network.");
        }
    }
}
