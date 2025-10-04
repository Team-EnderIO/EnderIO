package com.enderio.enderio.common.network.packets;

import com.enderio.enderio.common.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundSyncTravelDataPacket(CompoundTag data) implements CustomPacketPayload {
    public static final Type<ClientboundSyncTravelDataPacket> TYPE = new Type<>(EnderIO.rl("sync_travel_data"));

    public static final StreamCodec<ByteBuf, ClientboundSyncTravelDataPacket> STREAM_CODEC =
        ByteBufCodecs.COMPOUND_TAG.map(ClientboundSyncTravelDataPacket::new, ClientboundSyncTravelDataPacket::data);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
