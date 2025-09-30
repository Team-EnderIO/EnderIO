package com.enderio.machines.common.network.transceiver;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.transceiver.Channel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AddRemoveGlobalChannelPacket(Channel channel, boolean isAdd) implements CustomPacketPayload {

    public static final Type<AddRemoveGlobalChannelPacket> TYPE = new Type<>(EnderIO.loc("add_remove_global_channel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddRemoveGlobalChannelPacket> STREAM_CODEC = StreamCodec.composite(
        Channel.STREAM_CODEC,
        AddRemoveGlobalChannelPacket::channel,
        ByteBufCodecs.BOOL,
        AddRemoveGlobalChannelPacket::isAdd,
        AddRemoveGlobalChannelPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
