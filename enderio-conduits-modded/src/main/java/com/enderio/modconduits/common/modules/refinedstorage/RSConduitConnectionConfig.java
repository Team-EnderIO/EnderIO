package com.enderio.modconduits.common.modules.refinedstorage;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record RSConduitConnectionConfig(boolean isConnected) implements ConnectionConfig {

    public static final RSConduitConnectionConfig DEFAULT = new RSConduitConnectionConfig(true);

    public static final MapCodec<RSConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(Codec.BOOL.fieldOf("isConnected").forGetter(RSConduitConnectionConfig::isConnected))
                    .apply(inst, RSConduitConnectionConfig::new));

    public static final StreamCodec<ByteBuf, RSConduitConnectionConfig> STREAM_CODEC = ByteBufCodecs.BOOL
            .map(RSConduitConnectionConfig::new, RSConduitConnectionConfig::isConnected);

    public static final ConnectionConfigType<RSConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfigType<?> type() {
        return TYPE;
    }

    @Override
    public ConnectionConfig reconnected() {
        return new RSConduitConnectionConfig(true);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new RSConduitConnectionConfig(false);
    }
}
