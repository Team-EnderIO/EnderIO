package com.enderio.enderio.common.foundation.network.packets;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.common.content.conduits.probe.ConduitProbeItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ServerboundSyncProbeStatePacket(ConduitProbeItem.State state) implements CustomPacketPayload {

    public static final Type<ServerboundSyncProbeStatePacket> TYPE = new Type<>(EnderIO.rl("sync_probe_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSyncProbeStatePacket> STREAM_CODEC = StreamCodec.composite(
        ConduitProbeItem.State.STREAM_CODEC,
        ServerboundSyncProbeStatePacket::state,
        ServerboundSyncProbeStatePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
