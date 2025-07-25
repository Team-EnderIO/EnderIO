package com.enderio.machines.common.network.transceiver;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.transceiver.ChannelList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ChannelsSyncPacket(ChannelList channels) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ChannelsSyncPacket> TYPE = new CustomPacketPayload.Type<>(EnderIO.loc("channels_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChannelsSyncPacket> STREAM_CODEC = StreamCodec.composite(
        ChannelList.STREAM_CODEC,
        ChannelsSyncPacket::channels,
        ChannelsSyncPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
