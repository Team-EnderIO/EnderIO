package com.enderio.core.common.network.slot;

import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Supplier;

// TODO: Needs to accept a STREAM_CODEC instead of sending JSON over the network.
//       To be fair, I think I will rewrite network slots to be codec-only.
public class CodecNetworkDataSlot<T> extends NetworkDataSlot<T> {

    private final Codec<T> codec;

    public CodecNetworkDataSlot(Supplier<T> getter, Consumer<T> setter, Codec<T> codec) {
        super(getter, setter);
        this.codec = codec;
    }

    @Override
    public Tag serializeValueNBT(HolderLookup.Provider lookupProvider, T value) {
        return codec.encodeStart(NbtOps.INSTANCE, value).result().get();
    }

    @Override
    protected T valueFromNBT(HolderLookup.Provider lookupProvider, Tag nbt) {
        return codec.parse(NbtOps.INSTANCE, nbt).result().get();
    }

    @Override
    public void toBuffer(RegistryFriendlyByteBuf buf, T value) {
        buf.writeWithCodec(NbtOps.INSTANCE, codec, value);
    }

    @Override
    protected T valueFromBuffer(RegistryFriendlyByteBuf buf) {
        return buf.readWithCodecTrusted(NbtOps.INSTANCE, codec);
    }
}
