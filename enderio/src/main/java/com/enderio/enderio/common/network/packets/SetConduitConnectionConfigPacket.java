package com.enderio.enderio.common.network.packets;

import com.enderio.enderio.common.EnderIO;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record SetConduitConnectionConfigPacket(int containerId, ConnectionConfig connectionConfig)
        implements CustomPacketPayload {

    public static final Type<SetConduitConnectionConfigPacket> TYPE = new Type<>(
            EnderIO.rl("client_set_conduit_connection_config"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SetConduitConnectionConfigPacket> STREAM_CODEC = StreamCodec
            .composite(ByteBufCodecs.INT, SetConduitConnectionConfigPacket::containerId, ConnectionConfig.STREAM_CODEC,
                    SetConduitConnectionConfigPacket::connectionConfig, SetConduitConnectionConfigPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
