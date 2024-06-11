package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.network.DumbStreamCodec;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public class RedstoneConduitData implements ConduitData<RedstoneConduitData> {

    private boolean isActive = false;
    private final List<ColorControl> activeColors = new ArrayList<>();

    public RedstoneConduitData() {
    }

    private RedstoneConduitData(boolean isActive, List<ColorControl> activeColors) {
        this.isActive = isActive;
        this.activeColors.addAll(activeColors);
    }

    @Override
    public void applyClientChanges(RedstoneConduitData guiData) {
    }

    @Override
    public ConduitDataSerializer<RedstoneConduitData> serializer() {
        return EIOConduitTypes.Serializers.REDSTONE.get();
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isActive(ColorControl color) {
        return activeColors.contains(color);
    }

    public void clearActive() {
        activeColors.clear();
        isActive = false;
    }

    public List<ColorControl> activeColors() {
        return activeColors;
    }

    public void setActiveColor(ColorControl color) {
        if (activeColors.contains(color)) {
            return;
        }

        isActive = true;
        activeColors.add(color);
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    @Override
    public RedstoneConduitData deepCopy() {
        return new RedstoneConduitData(isActive, new ArrayList<>(activeColors));
    }

    public static class Serializer implements ConduitDataSerializer<RedstoneConduitData> {
        public static MapCodec<RedstoneConduitData> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                Codec.BOOL.fieldOf("is_active").forGetter(i -> i.isActive),
                ColorControl.CODEC.listOf().fieldOf("active_colors").forGetter(i -> i.activeColors)
            ).apply(instance, RedstoneConduitData::new)
        );

        public static StreamCodec<ByteBuf, RedstoneConduitData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            RedstoneConduitData::isActive,
            ColorControl.STREAM_CODEC.apply(ByteBufCodecs.list()),
            RedstoneConduitData::activeColors,
            RedstoneConduitData::new
        );

        @Override
        public MapCodec<RedstoneConduitData> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RedstoneConduitData> streamCodec() {
            return STREAM_CODEC.cast();
        }
    }
}
