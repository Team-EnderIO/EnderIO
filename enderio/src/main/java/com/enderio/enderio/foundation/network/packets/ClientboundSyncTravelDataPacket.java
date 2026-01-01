package com.enderio.enderio.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.travel.TravelTargetSavedData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundSyncTravelDataPacket(TravelTargetSavedData data) implements CustomPacketPayload {
    public static final Type<ClientboundSyncTravelDataPacket> TYPE = new Type<>(EnderIO.rl("sync_travel_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncTravelDataPacket> STREAM_CODEC =
        TravelTargetSavedData.STREAM_CODEC.map(ClientboundSyncTravelDataPacket::new, ClientboundSyncTravelDataPacket::data);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
