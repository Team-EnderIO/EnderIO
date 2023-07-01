package com.enderio.base.common.capability;

import com.enderio.api.capability.CoordinateSelection;
import com.enderio.api.capability.ICoordinateSelectionHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CoordinateSelectionHolder implements ICoordinateSelectionHolder {

    private final ItemStack stack;

    public CoordinateSelectionHolder(ItemStack stack) {
        this.stack = stack;
    }

    @Nullable
    @Override
    public CoordinateSelection getSelection() {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("Selection")) {
            CompoundTag selectionnbt = tag.getCompound("Selection");
            CoordinateSelection selection = new CoordinateSelection(new ResourceLocation(selectionnbt.getString("level")),
                NbtUtils.readBlockPos(selectionnbt.getCompound("pos")));
            return selection;
        }
        return null;
    }

    @Override
    public void setSelection(CoordinateSelection selection) {
        if (hasSelection()) {
            CompoundTag selectionnbt = new CompoundTag();
            selectionnbt.putString("level", selection.level().toString());
            selectionnbt.put("pos", NbtUtils.writeBlockPos(selection.pos()));
            CompoundTag stacktag = stack.getOrCreateTag();
            stacktag.put("Selection", selectionnbt);
        }

    }
}
