package com.enderio.modconduits.mods.mekanism.chemical;

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

public record ChemicalConduitConnectionConfig(boolean isSend, DyeColor sendColor, boolean isReceive, DyeColor receiveColor,
                                           RedstoneControl receiveRedstoneControl, DyeColor receiveRedstoneChannel)
    implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static ChemicalConduitConnectionConfig DEFAULT = new ChemicalConduitConnectionConfig(false, DyeColor.GREEN, true,
        DyeColor.GREEN, RedstoneControl.NEVER_ACTIVE, DyeColor.RED);

    public static MapCodec<ChemicalConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
        .group(Codec.BOOL.fieldOf("is_send").forGetter(ChemicalConduitConnectionConfig::isSend),
            DyeColor.CODEC.fieldOf("send_color").forGetter(ChemicalConduitConnectionConfig::sendColor),
            Codec.BOOL.fieldOf("is_receive").forGetter(ChemicalConduitConnectionConfig::isReceive),
            DyeColor.CODEC.fieldOf("receive_channel").forGetter(ChemicalConduitConnectionConfig::receiveColor),
            RedstoneControl.CODEC.fieldOf("receive_redstone_control")
                .forGetter(ChemicalConduitConnectionConfig::receiveRedstoneControl),
            DyeColor.CODEC.fieldOf("receive_redstone_channel")
                .forGetter(ChemicalConduitConnectionConfig::receiveRedstoneChannel))
        .apply(instance, ChemicalConduitConnectionConfig::new));

    // @formatter:off
    public static StreamCodec<ByteBuf, ChemicalConduitConnectionConfig> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL,
        ChemicalConduitConnectionConfig::isSend,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::sendColor,
        ByteBufCodecs.BOOL,
        ChemicalConduitConnectionConfig::isReceive,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::receiveColor,
        RedstoneControl.STREAM_CODEC,
        ChemicalConduitConnectionConfig::receiveRedstoneControl,
        DyeColor.STREAM_CODEC,
        ChemicalConduitConnectionConfig::receiveRedstoneChannel,
        ChemicalConduitConnectionConfig::new);
    // @formatter:on

    public static ConnectionConfigType<ChemicalConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
        STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new ChemicalConduitConnectionConfig(DEFAULT.isSend, sendColor, DEFAULT.isReceive, receiveColor,
            receiveRedstoneControl, receiveRedstoneChannel);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new ChemicalConduitConnectionConfig(false, sendColor, false, receiveColor, receiveRedstoneControl,
            receiveRedstoneChannel);
    }

    @Override
    public boolean canSend(ConduitRedstoneSignalAware signalAware) {
        // TODO: sendRedstoneControl
        return isSend();
    }

    @Override
    public boolean canReceive(ConduitRedstoneSignalAware signalAware) {
        if (!isReceive()) {
            return false;
        }

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

    public ChemicalConduitConnectionConfig withIsSend(boolean isSend) {
        return new ChemicalConduitConnectionConfig(isSend, sendColor, isReceive, receiveColor, receiveRedstoneControl,
            receiveRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withSendColor(DyeColor sendColor) {
        return new ChemicalConduitConnectionConfig(isSend, sendColor, isReceive, receiveColor, receiveRedstoneControl,
            receiveRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withIsReceive(boolean isReceive) {
        return new ChemicalConduitConnectionConfig(isSend, sendColor, isReceive, receiveColor, receiveRedstoneControl,
            receiveRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withReceiveColor(DyeColor receiveColor) {
        return new ChemicalConduitConnectionConfig(isSend, sendColor, isReceive, receiveColor, receiveRedstoneControl,
            receiveRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withReceiveRedstoneControl(RedstoneControl receiveRedstoneControl) {
        return new ChemicalConduitConnectionConfig(isSend, sendColor, isReceive, receiveColor, receiveRedstoneControl,
            receiveRedstoneChannel);
    }

    public ChemicalConduitConnectionConfig withReceiveRedstoneChannel(DyeColor receiveRedstoneChannel) {
        return new ChemicalConduitConnectionConfig(isSend, sendColor, isReceive, receiveColor, receiveRedstoneControl,
            receiveRedstoneChannel);
    }

    @Override
    public ConnectionConfigType<ChemicalConduitConnectionConfig> type() {
        return TYPE;
    }
}

