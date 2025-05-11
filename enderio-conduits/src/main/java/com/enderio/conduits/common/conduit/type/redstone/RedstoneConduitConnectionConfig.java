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

public record RedstoneConduitConnectionConfig(boolean isInsert, DyeColor insertColor, boolean isExtract,
                                              DyeColor extractColor, boolean isStrongOutputSignal) implements IOConnectionConfig {

    public static RedstoneConduitConnectionConfig DEFAULT = new RedstoneConduitConnectionConfig(false, DyeColor.GREEN,
            true, DyeColor.RED, false);

    public static MapCodec<RedstoneConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(RedstoneConduitConnectionConfig::isInsert),
                    DyeColor.CODEC.fieldOf("insert_color").forGetter(RedstoneConduitConnectionConfig::insertColor),
                    Codec.BOOL.fieldOf("is_extract").forGetter(RedstoneConduitConnectionConfig::isExtract),
                    DyeColor.CODEC.fieldOf("extract_color").forGetter(RedstoneConduitConnectionConfig::extractColor),
                    Codec.BOOL.fieldOf("is_strong_output_signal")
                            .forGetter(RedstoneConduitConnectionConfig::isStrongOutputSignal))
            .apply(instance, RedstoneConduitConnectionConfig::new));

    public static StreamCodec<ByteBuf, RedstoneConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, RedstoneConduitConnectionConfig::isInsert, DyeColor.STREAM_CODEC,
            RedstoneConduitConnectionConfig::insertColor, ByteBufCodecs.BOOL, RedstoneConduitConnectionConfig::isExtract,
            DyeColor.STREAM_CODEC, RedstoneConduitConnectionConfig::extractColor, ByteBufCodecs.BOOL,
            RedstoneConduitConnectionConfig::isStrongOutputSignal, RedstoneConduitConnectionConfig::new);

    public static ConnectionConfigType<RedstoneConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new RedstoneConduitConnectionConfig(DEFAULT.isInsert, insertColor, DEFAULT.isExtract, extractColor,
                isStrongOutputSignal);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new RedstoneConduitConnectionConfig(false, insertColor, false, extractColor, isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withIsInsert(boolean isInsert) {
        return new RedstoneConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withInsertColor(DyeColor insertColor) {
        return new RedstoneConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new RedstoneConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withExtractColor(DyeColor extractColor) {
        return new RedstoneConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, isStrongOutputSignal);
    }

    public RedstoneConduitConnectionConfig withIsStrongOutputSignal(boolean isStrongOutputSignal) {
        return new RedstoneConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, isStrongOutputSignal);
    }

    @Override
    public ConnectionConfigType<RedstoneConduitConnectionConfig> type() {
        return TYPE;
    }
}
