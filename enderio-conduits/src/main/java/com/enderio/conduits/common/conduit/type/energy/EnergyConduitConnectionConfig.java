package com.enderio.conduits.common.conduit.type.energy;

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

public record EnergyConduitConnectionConfig(boolean isInsert, boolean isExtract, RedstoneControl extractRedstoneControl,
        DyeColor extractRedstoneChannel) implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static EnergyConduitConnectionConfig DEFAULT = new EnergyConduitConnectionConfig(true, true,
            RedstoneControl.ALWAYS_ACTIVE, DyeColor.RED);

    public static MapCodec<EnergyConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(EnergyConduitConnectionConfig::isInsert),
                    Codec.BOOL.fieldOf("is_extract").forGetter(EnergyConduitConnectionConfig::isExtract),
                    RedstoneControl.CODEC.fieldOf("extract_redstone_control")
                            .forGetter(EnergyConduitConnectionConfig::extractRedstoneControl),
                    DyeColor.CODEC.fieldOf("extract_redstone_channel")
                            .forGetter(EnergyConduitConnectionConfig::extractRedstoneChannel))
            .apply(instance, EnergyConduitConnectionConfig::new));

    public static StreamCodec<ByteBuf, EnergyConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, EnergyConduitConnectionConfig::isInsert, ByteBufCodecs.BOOL,
            EnergyConduitConnectionConfig::isExtract, RedstoneControl.STREAM_CODEC,
            EnergyConduitConnectionConfig::extractRedstoneControl, DyeColor.STREAM_CODEC,
            EnergyConduitConnectionConfig::extractRedstoneChannel, EnergyConduitConnectionConfig::new);

    public static final ConnectionConfigType<EnergyConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new EnergyConduitConnectionConfig(DEFAULT.isInsert, DEFAULT.isExtract, extractRedstoneControl,
                extractRedstoneChannel);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new EnergyConduitConnectionConfig(false, false, extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public DyeColor insertChannel() {
        return DyeColor.RED;
    }

    @Override
    public DyeColor extractChannel() {
        return DyeColor.RED;
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

    public EnergyConduitConnectionConfig withIsInsert(boolean isInsert) {
        return new EnergyConduitConnectionConfig(isInsert, this.isExtract, extractRedstoneControl,
                extractRedstoneChannel);
    }

    public EnergyConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel);
    }

    public EnergyConduitConnectionConfig withExtractRedstoneControl(RedstoneControl extractRedstoneControl) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel);
    }

    public EnergyConduitConnectionConfig withExtractRedstoneChannel(DyeColor extractRedstoneChannel) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel);
    }

    @Override
    public ConnectionConfigType<?> type() {
        return TYPE;
    }
}
