package com.enderio.enderio.common.network.packets;

import com.enderio.enderio.common.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundBreakConduitPacket(BlockPos pos, Holder<Conduit<? ,?>> conduit) implements CustomPacketPayload {

    public static final Type<ServerboundBreakConduitPacket> TYPE = new Type<>(EnderIO.rl("break_conduit"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundBreakConduitPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        ServerboundBreakConduitPacket::pos,
        ByteBufCodecs.holderRegistry(EnderIORegistries.Keys.CONDUIT),
        ServerboundBreakConduitPacket::conduit,
        ServerboundBreakConduitPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
