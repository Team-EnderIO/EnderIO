package com.enderio.base.common.capability;

import com.enderio.api.capability.CoordinateSelection;
import com.enderio.api.capability.ICoordinateSelectionHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

public class CoordinateSelectionHolder implements ICoordinateSelectionHolder {

    @Nullable
    private CoordinateSelection selection;

    @Nullable
    @Override
    public CoordinateSelection getSelection() {
        return selection;
    }

    @Override
    public void setSelection(CoordinateSelection selection) {
        this.selection = selection;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        if (hasSelection()) {
            nbt.putString("level", selection.level().toString());
            nbt.put("pos", NbtUtils.writeBlockPos(selection.pos()));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag nbt && !nbt.isEmpty()) {
            selection = new CoordinateSelection(new ResourceLocation(nbt.getString("level")),
                NbtUtils.readBlockPos(nbt.getCompound("pos")));
        }
    }
}
