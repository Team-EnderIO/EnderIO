package com.enderio.core.common.network.slot;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CodecNetworkDataSlot<T> extends NetworkDataSlot<T> {

    private final Codec<T> codec;

    public CodecNetworkDataSlot(Supplier<T> getter, Consumer<T> setter, Codec<T> codec) {
        super(getter, setter);
        this.codec = codec;
    }

    @Override
    public Tag serializeValueNBT(T value) {
        return codec.encodeStart(NbtOps.INSTANCE, value).result().get();
    }

    @Override
    protected T valueFromNBT(Tag nbt) {
        return codec.parse(NbtOps.INSTANCE, nbt).result().get();
    }

    @Override
    public void toBuffer(FriendlyByteBuf buf, T value) {
        buf.writeWithCodec(NbtOps.INSTANCE, codec, value);
    }

    @Override
    protected T valueFromBuffer(FriendlyByteBuf buf) {
        return buf.readWithCodecTrusted(NbtOps.INSTANCE, codec);
    }
}
