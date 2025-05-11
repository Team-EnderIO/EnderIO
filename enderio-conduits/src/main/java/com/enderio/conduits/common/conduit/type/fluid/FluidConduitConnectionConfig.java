package com.enderio.conduits.common.conduit.type.fluid;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.api.ConduitRedstoneSignalAware;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.connection.config.IOConnectionConfig;
import com.enderio.conduits.api.connection.config.RedstoneSensitiveConnectionConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

public record FluidConduitConnectionConfig(boolean isInsert, DyeColor insertColor, boolean isExtract, DyeColor extractColor,
                                           RedstoneControl extractRedstoneControl, DyeColor extractRedstoneChannel)
        implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static FluidConduitConnectionConfig DEFAULT = new FluidConduitConnectionConfig(false, DyeColor.GREEN, true,
            DyeColor.GREEN, RedstoneControl.NEVER_ACTIVE, DyeColor.RED);

    public static MapCodec<FluidConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(FluidConduitConnectionConfig::isInsert),
                    DyeColor.CODEC.fieldOf("insert_color").forGetter(FluidConduitConnectionConfig::insertColor),
                    Codec.BOOL.fieldOf("is_extract").forGetter(FluidConduitConnectionConfig::isExtract),
                    DyeColor.CODEC.fieldOf("extract_channel").forGetter(FluidConduitConnectionConfig::extractColor),
                    RedstoneControl.CODEC.fieldOf("extract_redstone_control")
                            .forGetter(FluidConduitConnectionConfig::extractRedstoneControl),
                    DyeColor.CODEC.fieldOf("extract_redstone_channel")
                            .forGetter(FluidConduitConnectionConfig::extractRedstoneChannel))
            .apply(instance, FluidConduitConnectionConfig::new));

    // @formatter:off
    public static StreamCodec<ByteBuf, FluidConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        FluidConduitConnectionConfig::isInsert,
        DyeColor.STREAM_CODEC,
        FluidConduitConnectionConfig::insertColor,
        ByteBufCodecs.BOOL,
        FluidConduitConnectionConfig::isExtract,
        DyeColor.STREAM_CODEC,
        FluidConduitConnectionConfig::extractColor,
        RedstoneControl.STREAM_CODEC,
        FluidConduitConnectionConfig::extractRedstoneControl,
        DyeColor.STREAM_CODEC,
        FluidConduitConnectionConfig::extractRedstoneChannel,
        FluidConduitConnectionConfig::new);
    // @formatter:on

    public static ConnectionConfigType<FluidConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new FluidConduitConnectionConfig(DEFAULT.isInsert, insertColor, DEFAULT.isExtract, extractColor, extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new FluidConduitConnectionConfig(false, insertColor, false, extractColor, extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public boolean canInsert(ConduitRedstoneSignalAware signalAware) {
        // TODO: insertRedstoneControl
        return isInsert();
    }

    @Override
    public boolean canExtract(ConduitRedstoneSignalAware signalAware) {
        if (!isExtract()) {
            return false;
        }

        if (extractRedstoneControl.isRedstoneSensitive()) {
            return extractRedstoneControl.isActive(signalAware.hasRedstoneSignal(extractRedstoneChannel));
        } else {
            return extractRedstoneControl == RedstoneControl.ALWAYS_ACTIVE;
        }
    }

    @Override
    public List<DyeColor> getRedstoneSignalColors() {
        if (extractRedstoneControl.isRedstoneSensitive()) {
            return List.of(extractRedstoneChannel);
        }

        return List.of();
    }

    public FluidConduitConnectionConfig withIsInsert(boolean isInsert) {
        return new FluidConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl, extractRedstoneChannel);
    }

    public FluidConduitConnectionConfig withInsertColor(DyeColor insertColor) {
        return new FluidConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl, extractRedstoneChannel);
    }

    public FluidConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new FluidConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl, extractRedstoneChannel);
    }

    public FluidConduitConnectionConfig withExtractColor(DyeColor extractColor) {
        return new FluidConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl, extractRedstoneChannel);
    }

    public FluidConduitConnectionConfig withExtractRedstoneControl(RedstoneControl extractRedstoneControl) {
        return new FluidConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl, extractRedstoneChannel);
    }

    public FluidConduitConnectionConfig withExtractRedstoneChannel(DyeColor extractRedstoneChannel) {
        return new FluidConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public ConnectionConfigType<FluidConduitConnectionConfig> type() {
        return TYPE;
    }
}
