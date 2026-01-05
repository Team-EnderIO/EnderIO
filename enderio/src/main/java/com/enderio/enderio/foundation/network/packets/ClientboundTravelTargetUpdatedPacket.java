package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.travel.TravelTarget;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.Nullable;

public record ClientboundTravelTargetUpdatedPacket(@Nullable TravelTarget target) implements CustomPacketPayload {

    public static final Type<ClientboundTravelTargetUpdatedPacket> TYPE = new Type<>(EnderIO.id("add_travel_target"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTravelTargetUpdatedPacket> STREAM_CODEC = TravelTarget.STREAM_CODEC
        .map(ClientboundTravelTargetUpdatedPacket::new, ClientboundTravelTargetUpdatedPacket::target);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
