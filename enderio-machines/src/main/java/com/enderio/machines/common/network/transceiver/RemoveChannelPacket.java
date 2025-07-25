package com.enderio.machines.common.network.transceiver;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.transceiver.Channel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record RemoveChannelPacket(Channel channel) implements CustomPacketPayload {

    public static final Type<RemoveChannelPacket> TYPE = new Type<>(EnderIO.loc("delete_channel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RemoveChannelPacket> STREAM_CODEC = StreamCodec.composite(
        Channel.STREAM_CODEC,
        RemoveChannelPacket::channel,
        RemoveChannelPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
