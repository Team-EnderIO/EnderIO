package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.misc.ColorControl;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RedstoneConduitData implements ConduitData<RedstoneConduitData> {

    private boolean isActive = false;
    private final EnumMap<ColorControl, Integer> activeColors = new EnumMap<>(ColorControl.class);

    // region Serialization

    private static final String KEY_ACTIVE = "Active";
    private static final String KEY_COLORED_ACTIVE = "ColoredActive";
    private static final String KEY_ACTIVE_COLOR_MAP = "ActiveColors";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean(KEY_ACTIVE, isActive);

        CompoundTag activeColorMap = new CompoundTag();
        for (Map.Entry<ColorControl, Integer> entry : activeColors.entrySet()) {
            activeColorMap.put(entry.getKey().name(), IntTag.valueOf(entry.getValue()));
        }

        nbt.put(KEY_ACTIVE_COLOR_MAP, activeColorMap);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        isActive = nbt.getBoolean(KEY_ACTIVE);
        activeColors.clear();

        // Pre 6.1 compatibility - just populates with 15
        if (nbt.contains(KEY_COLORED_ACTIVE, Tag.TAG_LIST)) {
            ListTag list = nbt.getList(KEY_COLORED_ACTIVE, Tag.TAG_INT);
            for (Tag tag : list) {
                if (tag instanceof IntTag intTag) {
                    int intValue = intTag.getAsInt();
                    if (intValue < 0 || intValue >= ColorControl.values().length) {
                        continue;
                    }

                    ColorControl signalColor = ColorControl.values()[intValue];
                    activeColors.put(signalColor, 15);
                }
            }
        }

        // 6.1
        if (nbt.contains(KEY_ACTIVE_COLOR_MAP, Tag.TAG_COMPOUND)) {
            CompoundTag activeColorMap = nbt.getCompound(KEY_ACTIVE_COLOR_MAP);
            for (ColorControl color : ColorControl.values()) {
                if (activeColorMap.contains(color.name(), Tag.TAG_INT)) {
                    activeColors.put(color, activeColorMap.getInt(color.name()));
                } else {
                    activeColors.put(color, 0);
                }
            }
        }
    }

    // endregion

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

    public RedstoneConduitData deepCopy() {
        RedstoneConduitData redstoneConduitData = new RedstoneConduitData();
        redstoneConduitData.isActive = isActive;
        return redstoneConduitData;
    }
}
