package com.enderio.armory.common.item.darksteel.upgrades.flight;

import com.enderio.base.api.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record FlightEnabledPacket(boolean enabled) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FlightEnabledPacket> TYPE = new CustomPacketPayload.Type<>(
            EnderIO.loc("flight_upgrade_enabled"));

    public static final StreamCodec<ByteBuf, FlightEnabledPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL,
            FlightEnabledPacket::enabled, FlightEnabledPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
