package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.connection.config.IOConnectionConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

public record RedstoneConduitConnectionConfig(boolean isInsert, DyeColor insertChannel, boolean isExtract,
        DyeColor extractChannel, boolean isStrongOutputSignal) implements IOConnectionConfig {

    public static final RedstoneConduitConnectionConfig DEFAULT = new RedstoneConduitConnectionConfig(false, DyeColor.GREEN,
            true, DyeColor.RED, false);

    public static final MapCodec<RedstoneConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(RedstoneConduitConnectionConfig::isInsert),
                    DyeColor.CODEC.fieldOf("insert_channel").forGetter(RedstoneConduitConnectionConfig::insertChannel),
                    Codec.BOOL.fieldOf("is_extract").forGetter(RedstoneConduitConnectionConfig::isExtract),
                    DyeColor.CODEC.fieldOf("extract_channel")
                            .forGetter(RedstoneConduitConnectionConfig::extractChannel),
                    Codec.BOOL.fieldOf("is_strong_output_signal")
                            .forGetter(RedstoneConduitConnectionConfig::isStrongOutputSignal))
            .apply(instance, RedstoneConduitConnectionConfig::new));

    public static final StreamCodec<ByteBuf, RedstoneConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RedstoneConduitConnectionConfig::isInsert, DyeColor.STREAM_CODEC,
            RedstoneConduitConnectionConfig::insertChannel, ByteBufCodecs.BOOL,
            RedstoneConduitConnectionConfig::isExtract, DyeColor.STREAM_CODEC,
            RedstoneConduitConnectionConfig::extractChannel, ByteBufCodecs.BOOL,
            RedstoneConduitConnectionConfig::isStrongOutputSignal, RedstoneConduitConnectionConfig::new);

    public static final ConnectionConfigType<RedstoneConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new RedstoneConduitConnectionConfig(DEFAULT.isInsert, insertChannel, DEFAULT.isExtract, extractChannel,
                isStrongOutputSignal);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new RedstoneConduitConnectionConfig(false, insertChannel, false, extractChannel, isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withIsInsert(boolean isInsert) {
        return new RedstoneConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withInsertChannel(DyeColor insertChannel) {
        return new RedstoneConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new RedstoneConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withExtractChannel(DyeColor extractChannel) {
        return new RedstoneConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withIsStrongOutputSignal(boolean isStrongOutputSignal) {
        return new RedstoneConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                isStrongOutputSignal);
    }

    @Override
    public ConnectionConfigType<RedstoneConduitConnectionConfig> type() {
        return TYPE;
    }
}
