package com.enderio.api.conduit;

import com.enderio.api.registry.EnderIORegistries;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface ConduitData<T extends ConduitData<T>> {
    Codec<ConduitData<?>> CODEC = EnderIORegistries.CONDUIT_DATA_TYPE.byNameCodec()
        .dispatch(ConduitData::type, ConduitDataType::codec);
    StreamCodec<RegistryFriendlyByteBuf, ConduitData<?>> STREAM_CODEC = ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_DATA_TYPE)
        .dispatch(ConduitData::type, ConduitDataType::streamCodec);

    /**
     * Allows copying of data from a client change.
     * By default allows no changes.
     */
    default T withClientChanges(T guiData) {
        //noinspection unchecked
        return (T)this;
    }

    T deepCopy();

    ConduitDataType<T> type();
}
