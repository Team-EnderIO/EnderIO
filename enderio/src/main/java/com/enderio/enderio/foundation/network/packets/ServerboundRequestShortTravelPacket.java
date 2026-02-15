package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public record ServerboundRequestShortTravelPacket(Boolean unused) implements CustomPacketPayload {

    public static final Type<ServerboundRequestShortTravelPacket> TYPE = new Type<>(EnderIO.rl("request_short_travel"));

    public static final StreamCodec<ByteBuf, ServerboundRequestShortTravelPacket> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.BOOL, ServerboundRequestShortTravelPacket::unused, ServerboundRequestShortTravelPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
