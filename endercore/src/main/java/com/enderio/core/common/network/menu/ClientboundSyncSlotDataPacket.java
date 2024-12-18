package com.enderio.core.common.network.menu;

import com.enderio.core.EnderCore;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClientboundSyncSlotDataPacket(int containerId, List<PayloadPair> payloads)
        implements CustomPacketPayload {

    public static Type<ClientboundSyncSlotDataPacket> TYPE = new Type<>(EnderCore.loc("sync_slot_data"));

    public static StreamCodec<RegistryFriendlyByteBuf, ClientboundSyncSlotDataPacket> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, ClientboundSyncSlotDataPacket::containerId,
                    PayloadPair.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundSyncSlotDataPacket::payloads,
                    ClientboundSyncSlotDataPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record PayloadPair(short index, SlotPayload payload) {
        public static StreamCodec<RegistryFriendlyByteBuf, PayloadPair> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.SHORT, PayloadPair::index, SlotPayload.STREAM_CODEC, PayloadPair::payload,
                PayloadPair::new);
    }
}
