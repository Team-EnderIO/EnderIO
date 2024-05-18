package com.enderio.conduits.common.network;

import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SSetConduitExtendedData<T extends ExtendedConduitData<T>>(
    BlockPos pos,
    ConduitType<T> conduitType,
    ExtendedConduitData<T> extendedConduitData
) implements CustomPacketPayload {

    public static final Type<C2SSetConduitExtendedData<?>> TYPE = new Type<>(EnderCore.loc("c2s_conduit_extended_data"));

    public static StreamCodec<RegistryFriendlyByteBuf, C2SSetConduitExtendedData<?>> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        C2SSetConduitExtendedData::pos,
        ByteBufCodecs.registry(EnderIORegistries.Keys.CONDUIT_TYPES),
        C2SSetConduitExtendedData::conduitType,
        ExtendedConduitData.STREAM_CODEC,
        C2SSetConduitExtendedData::extendedConduitData,
        (pos, conduitType, extendedConduitData) -> new C2SSetConduitExtendedData<>(pos, conduitType, extendedConduitData.cast())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
