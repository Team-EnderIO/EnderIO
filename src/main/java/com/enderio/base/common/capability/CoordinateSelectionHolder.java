package com.enderio.base.common.capability;

import com.enderio.api.capability.CoordinateSelection;
import com.enderio.api.capability.ICoordinateSelectionHolder;
import com.enderio.base.EIONBTKeys;
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
        if (tag.contains(EIONBTKeys.COORDINATE_SELECTION)) {
            CompoundTag selectionnbt = tag.getCompound(EIONBTKeys.COORDINATE_SELECTION);
            CoordinateSelection selection = new CoordinateSelection(new ResourceLocation(selectionnbt.getString(EIONBTKeys.LEVEL)),
                NbtUtils.readBlockPos(selectionnbt.getCompound(EIONBTKeys.BLOCK_POS)));
            return selection;
        }
        return null;
    }

    @Override
    public void setSelection(CoordinateSelection selection) {
        if (hasSelection()) {
            CompoundTag selectionnbt = new CompoundTag();
            selectionnbt.putString(EIONBTKeys.LEVEL, selection.level().toString());
            selectionnbt.put(EIONBTKeys.BLOCK_POS, NbtUtils.writeBlockPos(selection.pos()));
            CompoundTag stacktag = stack.getOrCreateTag();
            stacktag.put(EIONBTKeys.COORDINATE_SELECTION, selectionnbt);
        }

    }
}
