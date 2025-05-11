package com.enderio.conduits.common.conduit.type.item;

import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.base.api.network.MassiveStreamCodec;
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

public record ItemConduitConnectionConfig(boolean isInsert, DyeColor insertColor, boolean isExtract, DyeColor extractColor,
                                          RedstoneControl extractRedstoneControl, DyeColor receiveRedstoneChannel, boolean isRoundRobin,
                                          boolean isSelfFeed, int priority) implements IOConnectionConfig, RedstoneSensitiveConnectionConfig {

    public static ItemConduitConnectionConfig DEFAULT = new ItemConduitConnectionConfig(false, DyeColor.GREEN, true,
            DyeColor.GREEN, RedstoneControl.NEVER_ACTIVE, DyeColor.RED, false, false, 0);

    public static MapCodec<ItemConduitConnectionConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.BOOL.fieldOf("is_insert").forGetter(ItemConduitConnectionConfig::isInsert),
                    DyeColor.CODEC.fieldOf("insert_color").forGetter(ItemConduitConnectionConfig::insertColor),
                    Codec.BOOL.fieldOf("is_extract").forGetter(ItemConduitConnectionConfig::isExtract),
                    DyeColor.CODEC.fieldOf("extract_color").forGetter(ItemConduitConnectionConfig::extractColor),
                    RedstoneControl.CODEC.fieldOf("extract_redstone_control")
                            .forGetter(ItemConduitConnectionConfig::extractRedstoneControl),
                    DyeColor.CODEC.fieldOf("extract_redstone_channel")
                            .forGetter(ItemConduitConnectionConfig::receiveRedstoneChannel),
                    Codec.BOOL.fieldOf("is_round_robin").forGetter(ItemConduitConnectionConfig::isRoundRobin),
                    Codec.BOOL.fieldOf("is_self_feed").forGetter(ItemConduitConnectionConfig::isSelfFeed),
                    Codec.INT.fieldOf("priority").forGetter(ItemConduitConnectionConfig::priority))
            .apply(instance, ItemConduitConnectionConfig::new));

    public static StreamCodec<ByteBuf, ItemConduitConnectionConfig> STREAM_CODEC = MassiveStreamCodec.composite(
            ByteBufCodecs.BOOL, ItemConduitConnectionConfig::isInsert, DyeColor.STREAM_CODEC,
            ItemConduitConnectionConfig::insertColor, ByteBufCodecs.BOOL, ItemConduitConnectionConfig::isExtract,
            DyeColor.STREAM_CODEC, ItemConduitConnectionConfig::extractColor, RedstoneControl.STREAM_CODEC,
            ItemConduitConnectionConfig::extractRedstoneControl, DyeColor.STREAM_CODEC,
            ItemConduitConnectionConfig::receiveRedstoneChannel, ByteBufCodecs.BOOL,
            ItemConduitConnectionConfig::isRoundRobin, ByteBufCodecs.BOOL, ItemConduitConnectionConfig::isSelfFeed,
            ByteBufCodecs.INT, ItemConduitConnectionConfig::priority, ItemConduitConnectionConfig::new);

    public static ConnectionConfigType<ItemConduitConnectionConfig> TYPE = new ConnectionConfigType<>(CODEC,
            STREAM_CODEC.cast(), () -> DEFAULT);

    @Override
    public ConnectionConfig reconnected() {
        return new ItemConduitConnectionConfig(false, insertColor, true, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    @Override
    public ConnectionConfig disconnected() {
        return new ItemConduitConnectionConfig(false, insertColor, false, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
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
            return extractRedstoneControl.isActive(signalAware.hasRedstoneSignal(receiveRedstoneChannel));
        } else {
            return extractRedstoneControl == RedstoneControl.ALWAYS_ACTIVE;
        }
    }

    @Override
    public List<DyeColor> getRedstoneSignalColors() {
        if (extractRedstoneControl.isRedstoneSensitive()) {
            return List.of(receiveRedstoneChannel);
        }

        return List.of();
    }

    public ItemConduitConnectionConfig withIsInsert(boolean isInsert) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    public ItemConduitConnectionConfig withInsertColor(DyeColor insertColor) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    public ItemConduitConnectionConfig withIsExtract(boolean isExtract) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    public ItemConduitConnectionConfig withExtractColor(DyeColor extractColor) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    public ItemConduitConnectionConfig withExtractRedstoneControl(RedstoneControl extractRedstoneControl) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    public ItemConduitConnectionConfig withExtractRedstoneChannel(DyeColor extractRedstoneChannel) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                extractRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    public ItemConduitConnectionConfig withIsRoundRobin(boolean isRoundRobin) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    public ItemConduitConnectionConfig withIsSelfFeed(boolean isSelfFeed) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, priority);
    }

    public ItemConduitConnectionConfig withPriority(int priority) {
        return new ItemConduitConnectionConfig(isInsert, insertColor, isExtract, extractColor, extractRedstoneControl,
                receiveRedstoneChannel, isRoundRobin, isSelfFeed, Math.min(9999, Math.max(-9999, priority)));
    }

    @Override
    public ConnectionConfigType<ItemConduitConnectionConfig> type() {
        return TYPE;
    }
}
