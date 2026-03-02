package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public record ServerboundToggleMagnetPacket() implements CustomPacketPayload {

    public static final Type<ServerboundToggleMagnetPacket> TYPE = new Type<>(EnderIO.rl("toggle_magnet"));

    public static final StreamCodec<ByteBuf, ServerboundToggleMagnetPacket> STREAM_CODEC =
        StreamCodec.unit(new ServerboundToggleMagnetPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

