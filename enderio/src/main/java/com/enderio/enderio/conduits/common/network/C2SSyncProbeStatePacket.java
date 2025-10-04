package com.enderio.enderio.conduits.common.network;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.common.conduits.probe.ConduitProbeItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record C2SSyncProbeStatePacket(ConduitProbeItem.State state) implements CustomPacketPayload {

    public static final Type<C2SSyncProbeStatePacket> TYPE = new Type<>(EnderIO.rl("sync_probe_state"));

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
