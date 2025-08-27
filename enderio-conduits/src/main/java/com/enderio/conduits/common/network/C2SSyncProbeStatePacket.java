package com.enderio.conduits.common.network;

import com.enderio.base.api.EnderIO;
import com.enderio.conduits.common.items.ConduitProbeItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SSyncProbeStatePacket(ConduitProbeItem.State state) implements CustomPacketPayload {

    public static final Type<C2SSyncProbeStatePacket> TYPE = new Type<>(EnderIO.loc("sync_probe_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, C2SSyncProbeStatePacket> STREAM_CODEC = StreamCodec.composite(
        ConduitProbeItem.State.STREAM_CODEC,
        C2SSyncProbeStatePacket::state,
        C2SSyncProbeStatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
