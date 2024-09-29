package com.enderio.base.common.network;

import com.enderio.EnderIOBase;
import com.enderio.base.api.travel.TravelTarget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.Nullable;

public record TravelTargetUpdatedPacket(@Nullable TravelTarget target) implements CustomPacketPayload {

    public static Type<TravelTargetUpdatedPacket> TYPE = new Type<>(EnderIOBase.loc("add_travel_target"));

    public static StreamCodec<RegistryFriendlyByteBuf, TravelTargetUpdatedPacket> STREAM_CODEC = TravelTarget.STREAM_CODEC
        .map(TravelTargetUpdatedPacket::new, TravelTargetUpdatedPacket::target);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
