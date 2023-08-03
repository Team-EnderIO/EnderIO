package com.enderio.conduits.common.types;

import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.misc.ColorControl;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.List;

public class RedstoneExtendedData implements IExtendedConduitData<RedstoneExtendedData> {

    private boolean isActive = false;
    private final List<ColorControl> activeColors = new ArrayList<>();

    // region Serialization

    private static final String KEY_ACTIVE = "Active";
    private static final String KEY_COLORED_ACTIVE = "ColoredActive";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean(KEY_ACTIVE, isActive);

        ListTag colors = new ListTag();
        for (ColorControl activeColor : activeColors) {
            colors.add(IntTag.valueOf(activeColor.ordinal()));
        }
        nbt.put(KEY_COLORED_ACTIVE, colors);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        isActive = nbt.getBoolean(KEY_ACTIVE);
        activeColors.clear();
        if (nbt.contains(KEY_COLORED_ACTIVE, Tag.TAG_LIST)) {
            ListTag list = nbt.getList(KEY_COLORED_ACTIVE, Tag.TAG_INT);
            for (Tag tag : list) {
                if (tag instanceof IntTag intTag) {
                    int intValue = intTag.getAsInt();
                    if (intValue < 0 || intValue >= ColorControl.values().length)
                        continue;
                    activeColors.add(ColorControl.values()[intValue]);
                }
            }
        }
    }

    // endregion

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
        if (activeColors.contains(color))
            return;
        isActive = true;
        activeColors.add(color);
    }

    public RedstoneExtendedData deepCopy() {
        RedstoneExtendedData redstoneExtendedData = new RedstoneExtendedData();
        redstoneExtendedData.isActive = isActive;
        return redstoneExtendedData;
    }
}