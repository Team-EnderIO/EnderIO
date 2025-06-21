package com.enderio.armory.common.item.darksteel.upgrades.travel;

import com.enderio.base.api.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TravelEndabledUpdatePacket(boolean enabled) implements CustomPacketPayload {

    public static final Type<TravelEndabledUpdatePacket> TYPE = new Type<>(EnderIO.loc("travel_status_update"));

    public static final StreamCodec<ByteBuf, TravelEndabledUpdatePacket> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.BOOL, TravelEndabledUpdatePacket::enabled, TravelEndabledUpdatePacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
