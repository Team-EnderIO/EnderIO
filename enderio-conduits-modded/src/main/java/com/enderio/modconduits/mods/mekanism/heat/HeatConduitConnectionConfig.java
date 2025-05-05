package com.enderio.modconduits.mods.mekanism.heat;

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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;

import java.util.List;

public record HeatConduitConnectionConfig(boolean isSend, boolean isReceive, RedstoneControl receiveRedstoneControl, DyeColor receiveRedstoneChannel)
    implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static HeatConduitConnectionConfig DEFAULT = new HeatConduitConnectionConfig(true, true, RedstoneControl.ALWAYS_ACTIVE, DyeColor.RED);

    public static MapCodec<HeatConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(Codec.BOOL.fieldOf("is_send").forGetter(HeatConduitConnectionConfig::isSend),
            Codec.BOOL.fieldOf("is_receive").forGetter(HeatConduitConnectionConfig::isReceive),
            RedstoneControl.CODEC.fieldOf("receive_redstone_control")
                .forGetter(HeatConduitConnectionConfig::receiveRedstoneControl),
            DyeColor.CODEC.fieldOf("receive_redstone_channel")
                .forGetter(HeatConduitConnectionConfig::receiveRedstoneChannel)
        ).apply(inst, HeatConduitConnectionConfig::new));

    // @formatter:off
    public static StreamCodec<ByteBuf, HeatConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        HeatConduitConnectionConfig::isSend,
        ByteBufCodecs.BOOL,
        HeatConduitConnectionConfig::isReceive,
        RedstoneControl.STREAM_CODEC,
        HeatConduitConnectionConfig::receiveRedstoneControl,
        DyeColor.STREAM_CODEC,
        HeatConduitConnectionConfig::receiveRedstoneChannel,
        HeatConduitConnectionConfig::new);
    // @formatter:on

    public static ConnectionConfigType<HeatConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
        STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public DyeColor sendColor() {
        return DyeColor.RED;
    }

    @Override
    public DyeColor receiveColor() {
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
    public boolean canReceive(ConduitRedstoneSignalAware signalAware) {
        if (!isReceive()) {
            return false;
        }

        // TODO: VERIFY THIS BEHAVIOUR. I THINK IT MAY BE WRONG.
        if (receiveRedstoneControl.isRedstoneSensitive()) {
            return receiveRedstoneControl.isActive(signalAware.hasRedstoneSignal(receiveRedstoneChannel));
        } else {
            return receiveRedstoneControl == RedstoneControl.ALWAYS_ACTIVE;
        }
    }

    @Override
    public List<DyeColor> getRedstoneSignalColors() {
        if (receiveRedstoneControl.isRedstoneSensitive()) {
            return List.of(receiveRedstoneChannel);
        }

        return List.of();
    }

    public HeatConduitConnectionConfig withIsSend(boolean isSend) {
        return new HeatConduitConnectionConfig(isSend, isReceive, receiveRedstoneControl, receiveRedstoneChannel);
    }

    public HeatConduitConnectionConfig withIsReceive(boolean isReceive) {
        return new HeatConduitConnectionConfig(isSend, isReceive, receiveRedstoneControl,receiveRedstoneChannel);
    }

    public HeatConduitConnectionConfig withReceiveRedstoneControl(RedstoneControl receiveRedstoneControl) {
        return new HeatConduitConnectionConfig(isSend, isReceive, receiveRedstoneControl,receiveRedstoneChannel);
    }

    public HeatConduitConnectionConfig withReceiveRedstoneChannel(DyeColor receiveRedstoneChannel) {
        return new HeatConduitConnectionConfig(isSend, isReceive, receiveRedstoneControl, receiveRedstoneChannel);
    }

    @Override
    public ConnectionConfigType<?> type() {
        return TYPE;
    }
}
