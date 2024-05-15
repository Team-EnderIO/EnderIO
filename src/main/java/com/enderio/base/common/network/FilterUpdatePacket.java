package com.enderio.base.common.network;

import com.enderio.EnderIO;
import com.enderio.base.common.init.EIOCapabilities;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FilterUpdatePacket(boolean nbt, boolean inverted) implements CustomPacketPayload {

    public static Type<FilterUpdatePacket> TYPE = new Type<>(EnderIO.loc("filter_update"));

    public static StreamCodec<ByteBuf, FilterUpdatePacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        FilterUpdatePacket::nbt,
        ByteBufCodecs.BOOL,
        FilterUpdatePacket::inverted,
        FilterUpdatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
