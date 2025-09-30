package com.enderio.enderio.conduits.common.network;

import com.enderio.enderio.api.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.EnderIORegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SBreakConduitPacket(BlockPos pos, Holder<Conduit<? ,?>> conduit) implements CustomPacketPayload {

    public static final Type<C2SBreakConduitPacket> TYPE = new Type<>(EnderIO.loc("break_conduit"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SBreakConduitPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        C2SBreakConduitPacket::pos,
        ByteBufCodecs.holderRegistry(EnderIORegistries.Keys.CONDUIT),
        C2SBreakConduitPacket::conduit,
        C2SBreakConduitPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
