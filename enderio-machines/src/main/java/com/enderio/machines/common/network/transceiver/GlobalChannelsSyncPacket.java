package com.enderio.machines.common.network.transceiver;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.transceiver.ChannelList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GlobalChannelsSyncPacket(ChannelList channels) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<GlobalChannelsSyncPacket> TYPE = new CustomPacketPayload.Type<>(EnderIO.loc("channels_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GlobalChannelsSyncPacket> STREAM_CODEC = StreamCodec.composite(
        ChannelList.STREAM_CODEC,
        GlobalChannelsSyncPacket::channels,
        GlobalChannelsSyncPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
