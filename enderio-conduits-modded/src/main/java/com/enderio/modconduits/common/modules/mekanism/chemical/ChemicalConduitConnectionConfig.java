package com.enderio.modconduits.common.modules.mekanism.chemical;

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

public record ChemicalConduitConnectionConfig(boolean isInsert, DyeColor insertColor, boolean isExtract,
        DyeColor extractColor, RedstoneControl extractRedstoneControl, DyeColor extractRedstoneChannel)
        implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static ChemicalConduitConnectionConfig DEFAULT = new ChemicalConduitConnectionConfig(false, DyeColor.GREEN,
            true, DyeColor.GREEN, RedstoneControl.NEVER_ACTIVE, DyeColor.RED);

    public static MapCodec<ChemicalConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(ChemicalConduitConnectionConfig::isInsert),
                    DyeColor.CODEC.fieldOf("insert_color").forGetter(ChemicalConduitConnectionConfig::insertColor),
                    Codec.BOOL.fieldOf("is_extract").forGetter(ChemicalConduitConnectionConfig::isExtract),
                    DyeColor.CODEC.fieldOf("extract_color").forGetter(ChemicalConduitConnectionConfig::extractColor),
                    RedstoneControl.CODEC.fieldOf("extract_redstone_control")
                            .forGetter(ChemicalConduitConnectionConfig::extractRedstoneControl),
                    DyeColor.CODEC.fieldOf("extract_redstone_channel")
                            .forGetter(ChemicalConduitConnectionConfig::extractRedstoneChannel))
            .apply(instance, ChemicalConduitConnectionConfig::new));

    // @formatter:off
    public static StreamCodec<ByteBuf, ChemicalConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ChemicalConduitConnectionConfig::isInsert,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::insertColor,
        ByteBufCodecs.BOOL,
        ChemicalConduitConnectionConfig::isExtract,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::extractColor,
        RedstoneControl.STREAM_CODEC,
        ChemicalConduitConnectionConfig::extractRedstoneControl,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::extractRedstoneChannel,
        ChemicalConduitConnectionConfig::new);
    // @formatter:on

    public static ConnectionConfigType<ChemicalConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new ChemicalConduitConnectionConfig(DEFAULT.isInsert, insertColor, DEFAULT.isExtract, extractColor,
                extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new ChemicalConduitConnectionConfig(false, insertColor, false, extractColor, extractRedstoneControl,
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
        return new ChemicalConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withInsertColor(DyeColor insertColor) {
        return new ChemicalConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new ChemicalConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withExtractColor(DyeColor extractColor) {
        return new ChemicalConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withExtractRedstoneControl(RedstoneControl extractRedstoneControl) {
        return new ChemicalConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor,
                extractRedstoneControl, extractRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withExtractRedstoneChannel(DyeColor extractRedstoneChannel) {
        return new ChemicalConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor,
                extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public ConnectionConfigType<ChemicalConduitConnectionConfig> type() {
        return TYPE;
    }
}
