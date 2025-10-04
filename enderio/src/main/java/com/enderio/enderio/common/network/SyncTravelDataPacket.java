package com.enderio.enderio.common.network;

import com.enderio.enderio.common.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SyncTravelDataPacket(CompoundTag data) implements CustomPacketPayload {
    public static final Type<SyncTravelDataPacket> TYPE = new Type<>(EnderIO.rl("sync_travel_data"));

    public static final StreamCodec<ByteBuf, SyncTravelDataPacket> STREAM_CODEC =
        ByteBufCodecs.COMPOUND_TAG.map(SyncTravelDataPacket::new, SyncTravelDataPacket::data);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
