package com.enderio.enderio.armory.common.item.darksteel.upgrades.speed;

import com.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SpeedUsePowerPacket(int energyUse) implements CustomPacketPayload {

    public static final Type<SpeedUsePowerPacket> TYPE = new Type<>(EnderIO.rl("speed_upgrade_use_energy"));

    public static final StreamCodec<ByteBuf, SpeedUsePowerPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.INT,
            SpeedUsePowerPacket::energyUse, SpeedUsePowerPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
