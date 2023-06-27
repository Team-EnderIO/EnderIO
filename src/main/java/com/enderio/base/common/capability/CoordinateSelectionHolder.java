package com.enderio.base.common.capability;

import com.enderio.api.capability.CoordinateSelection;
import com.enderio.api.capability.ICoordinateSelectionHolder;
import com.enderio.base.EIONBTKeys;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

public class CoordinateSelectionHolder implements ICoordinateSelectionHolder, INBTSerializable<Tag> {

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
            nbt.putString(EIONBTKeys.LEVEL, selection.level().toString());
            nbt.put(EIONBTKeys.BLOCK_POS, NbtUtils.writeBlockPos(selection.pos()));
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag nbt && !nbt.isEmpty()) {
            selection = new CoordinateSelection(new ResourceLocation(nbt.getString(EIONBTKeys.LEVEL)),
                NbtUtils.readBlockPos(nbt.getCompound(EIONBTKeys.BLOCK_POS)));
        }
    }
}
