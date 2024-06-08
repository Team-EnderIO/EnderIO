package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedstoneExtendedData implements ExtendedConduitData<RedstoneExtendedData> {

    private boolean isActive = false;
    private final EnumMap<ColorControl, Integer> activeColors = new EnumMap<>(ColorControl.class);

    public RedstoneExtendedData() {
    }

    private RedstoneExtendedData(boolean isActive, Map<ColorControl, Integer> activeColors) {
        this.isActive = isActive;
        this.activeColors.putAll(activeColors);
    }

    @Override
    public void applyGuiChanges(RedstoneExtendedData guiData) {
    }

    @Override
    public ConduitDataSerializer<RedstoneExtendedData> serializer() {
        return EIOConduitTypes.Serializers.REDSTONE.get();
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isActive(ColorControl color) {
        return activeColors.containsKey(color);
    }

    public int getSignal(ColorControl color) {
        return activeColors.getOrDefault(color, 0);
    }

    public void clearActive() {
        activeColors.clear();
        isActive = false;
    }

    public void setActiveColor(ColorControl color, int signal) {
        if (activeColors.containsKey(color)) {
            return;
        }

        isActive = true;
        activeColors.put(color, signal);
    }

    public RedstoneExtendedData deepCopy() {
        RedstoneExtendedData redstoneExtendedData = new RedstoneExtendedData();
        redstoneExtendedData.isActive = isActive;
        return redstoneExtendedData;
    }

    public static class Serializer implements ConduitDataSerializer<RedstoneExtendedData> {
        public static MapCodec<RedstoneExtendedData> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.BOOL.fieldOf("is_active").forGetter(i -> i.isActive),
                Codec.unboundedMap(ColorControl.CODEC, Codec.INT).fieldOf("active_colors").forGetter(i -> i.activeColors)
            ).apply(instance, RedstoneExtendedData::new)
        );

        public static StreamCodec<ByteBuf, RedstoneExtendedData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            r -> r.isActive,
            ByteBufCodecs.map(HashMap::new, ColorControl.STREAM_CODEC, ByteBufCodecs.INT),
            r -> r.activeColors,
            RedstoneExtendedData::new
        );

        @Override
        public MapCodec<RedstoneExtendedData> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RedstoneExtendedData> streamCodec() {
            return STREAM_CODEC.cast();
        }
    }
}
