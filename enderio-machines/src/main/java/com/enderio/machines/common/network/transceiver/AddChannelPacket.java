package com.enderio.machines.common.network.transceiver;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.transceiver.ChannelType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AddChannelPacket(String name, String owner, ChannelType channelType, boolean isPrivate) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<AddChannelPacket> TYPE = new CustomPacketPayload.Type<>(EnderIO.loc("add_channel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddChannelPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        AddChannelPacket::name,
        ByteBufCodecs.STRING_UTF8,
        AddChannelPacket::owner,
        ChannelType.STREAM_CODEC,
        AddChannelPacket::channelType,
        ByteBufCodecs.BOOL,
        AddChannelPacket::isPrivate,
        AddChannelPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
