package com.enderio.machines.common.network.transceiver;

import com.enderio.base.api.EnderIO;
import com.enderio.machines.common.transceiver.Channel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record AddRemoveTransceiverChannelPacket(BlockPos pos, Channel channel, boolean isAdd, boolean isSend, boolean isReceive) implements CustomPacketPayload {

    public static final Type<AddRemoveTransceiverChannelPacket> TYPE = new Type<>(EnderIO.loc("add_remove_transceiver_channel"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AddRemoveTransceiverChannelPacket> STREAM_CODEC = StreamCodec.composite(
        BlockPos.STREAM_CODEC,
        AddRemoveTransceiverChannelPacket::pos,
        Channel.STREAM_CODEC,
        AddRemoveTransceiverChannelPacket::channel,
        ByteBufCodecs.BOOL,
        AddRemoveTransceiverChannelPacket::isAdd,
        ByteBufCodecs.BOOL,
        AddRemoveTransceiverChannelPacket::isSend,
        ByteBufCodecs.BOOL,
        AddRemoveTransceiverChannelPacket::isReceive,
        AddRemoveTransceiverChannelPacket::new
    );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
