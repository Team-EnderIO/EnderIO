package com.enderio.modded_conduits.common.modules.mekanism.chemical;

import com.enderio.enderio.api.conduits.ConduitRedstoneSignalAware;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.connection.config.IOConnectionConfig;
import com.enderio.enderio.api.conduits.connection.config.RedstoneSensitiveConnectionConfig;
import com.enderio.enderio.api.io.RedstoneControl;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

import java.util.List;

public record ChemicalConduitConnectionConfig(boolean isInsert, DyeColor insertChannel, boolean isExtract,
        DyeColor extractChannel, RedstoneControl extractRedstoneControl, DyeColor extractRedstoneChannel)
        implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static final ChemicalConduitConnectionConfig DEFAULT = new ChemicalConduitConnectionConfig(false, DyeColor.GREEN,
            true, DyeColor.GREEN, RedstoneControl.NEVER_ACTIVE, DyeColor.RED);

    public static final MapCodec<ChemicalConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(ChemicalConduitConnectionConfig::isInsert),
                    DyeColor.CODEC.fieldOf("insert_channel").forGetter(ChemicalConduitConnectionConfig::insertChannel),
                    Codec.BOOL.fieldOf("is_extract").forGetter(ChemicalConduitConnectionConfig::isExtract),
                    DyeColor.CODEC.fieldOf("extract_channel")
                            .forGetter(ChemicalConduitConnectionConfig::extractChannel),
                    RedstoneControl.CODEC.fieldOf("extract_redstone_control")
                            .forGetter(ChemicalConduitConnectionConfig::extractRedstoneControl),
                    DyeColor.CODEC.fieldOf("extract_redstone_channel")
                            .forGetter(ChemicalConduitConnectionConfig::extractRedstoneChannel))
            .apply(instance, ChemicalConduitConnectionConfig::new));

    // @formatter:off
    public static final StreamCodec<ByteBuf, ChemicalConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ChemicalConduitConnectionConfig::isInsert,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::insertChannel,
        ByteBufCodecs.BOOL,
        ChemicalConduitConnectionConfig::isExtract,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::extractChannel,
        RedstoneControl.STREAM_CODEC,
        ChemicalConduitConnectionConfig::extractRedstoneControl,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::extractRedstoneChannel,
        ChemicalConduitConnectionConfig::new);
    // @formatter:on

    public static final ConnectionConfigType<ChemicalConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new ChemicalConduitConnectionConfig(DEFAULT.isInsert, insertChannel, DEFAULT.isExtract, extractChannel,
                extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new ChemicalConduitConnectionConfig(false, insertChannel, false, extractChannel, extractRedstoneControl,
                extractRedstoneChannel);
    }

    @Override
    public boolean canInsert(ConduitRedstoneSignalAware signalAware) {
        // TODO: sendRedstoneControl
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

    public ChemicalConduitConnectionConfig withIsInsert(boolean isInsert) {
        return new ChemicalConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withInsertChannel(DyeColor insertChannel) {
        return new ChemicalConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new ChemicalConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withExtractChannel(DyeColor extractChannel) {
        return new ChemicalConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withExtractRedstoneControl(RedstoneControl extractRedstoneControl) {
        return new ChemicalConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withExtractRedstoneChannel(DyeColor extractRedstoneChannel) {
        return new ChemicalConduitConnectionConfig(isInsert, insertChannel, isExtract, extractChannel,
                extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public ConnectionConfigType<ChemicalConduitConnectionConfig> type() {
        return TYPE;
    }
}
