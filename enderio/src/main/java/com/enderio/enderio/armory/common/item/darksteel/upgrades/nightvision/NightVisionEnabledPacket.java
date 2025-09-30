package com.enderio.enderio.armory.common.item.darksteel.upgrades.nightvision;

import com.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record NightVisionEnabledPacket(boolean enabled) implements CustomPacketPayload {

    public static final Type<NightVisionEnabledPacket> TYPE = new Type<>(EnderIO.rl("nightvision_upgrade_enabled"));

    public static final StreamCodec<ByteBuf, NightVisionEnabledPacket> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.BOOL, NightVisionEnabledPacket::enabled, NightVisionEnabledPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
