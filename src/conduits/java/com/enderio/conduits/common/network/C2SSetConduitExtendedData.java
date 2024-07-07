package com.enderio.conduits.common.network;

import com.enderio.api.conduit.Conduit;
import com.enderio.conduits.common.conduit.ConduitDataContainer;
import com.enderio.core.EnderCore;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SSetConduitExtendedData(
    BlockPos pos,
    Holder<Conduit<?>> conduit,
    ConduitDataContainer conduitDataContainer
) implements CustomPacketPayload {

    public static final Type<C2SSetConduitExtendedData> TYPE = new Type<>(EnderCore.loc("c2s_conduit_extended_data"));

    public static StreamCodec<RegistryFriendlyByteBuf, C2SSetConduitExtendedData> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        C2SSetConduitExtendedData::pos,
        Conduit.STREAM_CODEC,
        C2SSetConduitExtendedData::conduit,
        ConduitDataContainer.STREAM_CODEC,
        C2SSetConduitExtendedData::conduitDataContainer,
        C2SSetConduitExtendedData::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
