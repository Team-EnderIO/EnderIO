package com.enderio.modconduits.common.modules.appeng;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MEConduitConnectionConfig(boolean isConnected) implements ConnectionConfig {

    public static final MEConduitConnectionConfig DEFAULT = new MEConduitConnectionConfig(true);

    public static final MapCodec<MEConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(Codec.BOOL.fieldOf("isConnected").forGetter(MEConduitConnectionConfig::isConnected))
                    .apply(inst, MEConduitConnectionConfig::new));

    public static final StreamCodec<ByteBuf, MEConduitConnectionConfig> STREAM_CODEC = ByteBufCodecs.BOOL
            .map(MEConduitConnectionConfig::new, MEConduitConnectionConfig::isConnected);

    public static final ConnectionConfigType<MEConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfigType<?> type() {
        return TYPE;
    }

    @Override
    public ConnectionConfig reconnected() {
        return new MEConduitConnectionConfig(true);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new MEConduitConnectionConfig(false);
    }
}
