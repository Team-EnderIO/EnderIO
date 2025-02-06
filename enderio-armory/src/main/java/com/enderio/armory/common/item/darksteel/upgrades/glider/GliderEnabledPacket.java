package com.enderio.armory.common.item.darksteel.upgrades.glider;

import com.enderio.base.api.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GliderEnabledPacket(boolean enabled) implements CustomPacketPayload {

    public static CustomPacketPayload.Type<GliderEnabledPacket> TYPE = new CustomPacketPayload.Type<>(
            EnderIO.loc("glider_upgrade_enabled"));

    public static StreamCodec<ByteBuf, GliderEnabledPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL,
            GliderEnabledPacket::enabled, GliderEnabledPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
