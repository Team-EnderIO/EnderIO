package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.conduits.api.ConduitRedstoneSignalAware;
import com.enderio.conduits.api.connection.config.ConnectionConfig;
import com.enderio.conduits.api.connection.config.ConnectionConfigType;
import com.enderio.conduits.api.connection.config.IOConnectionConfig;
import com.enderio.conduits.api.connection.config.RedstoneSensitiveConnectionConfig;
import com.enderio.conduits.common.conduit.type.item.ItemConduitConnectionConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

public record EnergyConduitConnectionConfig(boolean isInsert, boolean isExtract, RedstoneControl extractRedstoneControl,
        DyeColor extractRedstoneChannel, int priority) implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static final EnergyConduitConnectionConfig DEFAULT = new EnergyConduitConnectionConfig(true, true,
            RedstoneControl.ALWAYS_ACTIVE, DyeColor.RED, 0);

    public static final MapCodec<EnergyConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(EnergyConduitConnectionConfig::isInsert),
                    Codec.BOOL.fieldOf("is_extract").forGetter(EnergyConduitConnectionConfig::isExtract),
                    RedstoneControl.CODEC.fieldOf("extract_redstone_control")
                            .forGetter(EnergyConduitConnectionConfig::extractRedstoneControl),
                    DyeColor.CODEC.fieldOf("extract_redstone_channel")
                            .forGetter(EnergyConduitConnectionConfig::extractRedstoneChannel),
                Codec.INT.optionalFieldOf("priority", 0).forGetter(EnergyConduitConnectionConfig::priority))
            .apply(instance, EnergyConduitConnectionConfig::new));

    public static final StreamCodec<ByteBuf, EnergyConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, EnergyConduitConnectionConfig::isInsert, ByteBufCodecs.BOOL,
            EnergyConduitConnectionConfig::isExtract, RedstoneControl.STREAM_CODEC,
            EnergyConduitConnectionConfig::extractRedstoneControl, DyeColor.STREAM_CODEC,
            EnergyConduitConnectionConfig::extractRedstoneChannel, ByteBufCodecs.INT,
            EnergyConduitConnectionConfig::priority, EnergyConduitConnectionConfig::new);

    public static final ConnectionConfigType<EnergyConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new EnergyConduitConnectionConfig(DEFAULT.isInsert, DEFAULT.isExtract, extractRedstoneControl,
                extractRedstoneChannel, priority);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new EnergyConduitConnectionConfig(false, false, extractRedstoneControl, extractRedstoneChannel, priority);
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
                extractRedstoneChannel, priority);
    }

    public EnergyConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel, priority);
    }

    public EnergyConduitConnectionConfig withExtractRedstoneControl(RedstoneControl extractRedstoneControl) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel, priority);
    }

    public EnergyConduitConnectionConfig withExtractRedstoneChannel(DyeColor extractRedstoneChannel) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel, priority);
    }

    public EnergyConduitConnectionConfig withPriority(int priority) {
        return new EnergyConduitConnectionConfig(isInsert, isExtract, extractRedstoneControl, extractRedstoneChannel, priority);
    }

    @Override
    public ConnectionConfigType<?> type() {
        return TYPE;
    }
}
