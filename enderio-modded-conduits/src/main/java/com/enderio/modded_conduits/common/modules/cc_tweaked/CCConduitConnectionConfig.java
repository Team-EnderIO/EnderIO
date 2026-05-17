package com.enderio.modded_conduits.common.modules.cc_tweaked;

import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CCConduitConnectionConfig(boolean isConnected) implements ConnectionConfig {

    public static final CCConduitConnectionConfig DEFAULT = new CCConduitConnectionConfig(true);

    public static final MapCodec<CCConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(Codec.BOOL.fieldOf("isConnected").forGetter(CCConduitConnectionConfig::isConnected))
                    .apply(inst, CCConduitConnectionConfig::new));

    public static final StreamCodec<ByteBuf, CCConduitConnectionConfig> STREAM_CODEC = ByteBufCodecs.BOOL
            .map(CCConduitConnectionConfig::new, CCConduitConnectionConfig::isConnected);

    public static final ConnectionConfigType<CCConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfigType<?> type() {
        return TYPE;
    }

    @Override
    public ConnectionConfig reconnected() {
        return new CCConduitConnectionConfig(true);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new CCConduitConnectionConfig(false);
    }
}
