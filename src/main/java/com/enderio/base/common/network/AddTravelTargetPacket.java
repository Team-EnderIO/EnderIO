package com.enderio.base.common.network;

import com.enderio.EnderIO;
import com.enderio.api.travel.TravelTarget;
import com.enderio.api.travel.TravelRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

public record AddTravelTargetPacket(@Nullable TravelTarget target) implements CustomPacketPayload {

    public static Type<AddTravelTargetPacket> TYPE = new Type<>(EnderIO.loc("add_travel_target"));

    // TODO: 20.6: this is really really not great.
    public static StreamCodec<ByteBuf, AddTravelTargetPacket> STREAM_CODEC =
        ByteBufCodecs.COMPOUND_TAG.map(
            tag -> new AddTravelTargetPacket(TravelRegistry.deserialize(tag).orElseThrow()),
            value -> value.target() != null
                ? TravelRegistry.serialize(value.target())
                : new CompoundTag());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
