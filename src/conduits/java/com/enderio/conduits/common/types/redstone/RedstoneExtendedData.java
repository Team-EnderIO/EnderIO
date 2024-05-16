package com.enderio.conduits.common.types.redstone;

import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.init.ConduitTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;

public class RedstoneExtendedData implements ExtendedConduitData<RedstoneExtendedData> {

    private boolean isActive = false;
    private final List<ColorControl> activeColors = new ArrayList<>();

    public RedstoneExtendedData() {
    }

    private RedstoneExtendedData(boolean isActive, List<ColorControl> activeColors) {
        this.isActive = isActive;
        this.activeColors.addAll(activeColors);
    }

    @Override
    public void applyGuiChanges(RedstoneExtendedData guiData) {
        // TODO: Hmmmmmm
    }

    @Override
    public ConduitDataSerializer<RedstoneExtendedData> serializer() {
        return ConduitTypes.REDSTONE_DATA_SERIALIZER.get();
    }

    // TODO: Not accessed by anything - is it that redstone extended data hasn't worked??
    @Override
    public boolean syncDataToClient() {
        return true;
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

    public void setActiveColor(ColorControl color) {
        if (activeColors.contains(color)) {
            return;
        }

        isActive = true;
        activeColors.add(color);
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
                ColorControl.CODEC.listOf().fieldOf("active_colors").forGetter(i -> i.activeColors)
            ).apply(instance, RedstoneExtendedData::new)
        );

        @Override
        public MapCodec<RedstoneExtendedData> codec() {
            return CODEC;
        }
    }
}
