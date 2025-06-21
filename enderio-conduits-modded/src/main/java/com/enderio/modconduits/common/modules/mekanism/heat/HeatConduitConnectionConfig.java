package com.enderio.modconduits.common.modules.mekanism.heat;

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

public record HeatConduitConnectionConfig(boolean isInsert, boolean isExtract, RedstoneControl extractRedstoneControl,
        DyeColor extractRedstoneChannel) implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static final HeatConduitConnectionConfig DEFAULT = new HeatConduitConnectionConfig(true, true,
            RedstoneControl.ALWAYS_ACTIVE, DyeColor.RED);

    public static final MapCodec<HeatConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(HeatConduitConnectionConfig::isInsert),
                    Codec.BOOL.fieldOf("is_extract").forGetter(HeatConduitConnectionConfig::isExtract),
                    RedstoneControl.CODEC.fieldOf("extract_redstone_control")
                            .forGetter(HeatConduitConnectionConfig::extractRedstoneControl),
                    DyeColor.CODEC.fieldOf("extract_redstone_channel")
                            .forGetter(HeatConduitConnectionConfig::extractRedstoneChannel))
            .apply(inst, HeatConduitConnectionConfig::new));

    // @formatter:off
    public static final StreamCodec<ByteBuf, HeatConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        HeatConduitConnectionConfig::isInsert,
        ByteBufCodecs.BOOL,
        HeatConduitConnectionConfig::isExtract,
        RedstoneControl.STREAM_CODEC,
        HeatConduitConnectionConfig::extractRedstoneControl,
        DyeColor.STREAM_CODEC,
        HeatConduitConnectionConfig::extractRedstoneChannel,
        HeatConduitConnectionConfig::new);
    // @formatter:on

    public static final ConnectionConfigType<HeatConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public DyeColor insertChannel() {
        return DyeColor.RED;
    }

    @Override
    public DyeColor extractChannel() {
        return DyeColor.RED;
    }

    @Override
    public ConnectionConfig reconnected() {
        return null;
    }

    @Override
    public ConnectionConfig disconnected() {
        return null;
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

    public HeatConduitConnectionConfig withIsInsert(boolean isInsert) {
        return new HeatConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel);
    }

    public HeatConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new HeatConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel);
    }

    public HeatConduitConnectionConfig withExtractRedstoneControl(RedstoneControl extractRedstoneControl) {
        return new HeatConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel);
    }

    public HeatConduitConnectionConfig withExtractRedstoneChannel(DyeColor extractRedstoneChannel) {
        return new HeatConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public ConnectionConfigType<?> type() {
        return TYPE;
    }
}
