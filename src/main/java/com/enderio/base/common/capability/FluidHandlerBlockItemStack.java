package com.enderio.base.common.capability;

import com.enderio.base.common.util.NbtTags;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

public class FluidHandlerBlockItemStack extends FluidHandlerItemStack {
    public FluidHandlerBlockItemStack(@NotNull ItemStack container, int capacity) {
        super(container, capacity);
    }

    @Override
    @NotNull
    public FluidStack getFluid() {
        CompoundTag tagCompound = container.getTag();
        if (tagCompound == null || !tagCompound.contains(NbtTags.BLOCK_ENTITY_NBT_KEY)) {
            return FluidStack.EMPTY;
        }
        tagCompound = tagCompound.getCompound(NbtTags.BLOCK_ENTITY_NBT_KEY);
        if (!tagCompound.contains(FLUID_NBT_KEY)) {
            return FluidStack.EMPTY;
        }
        return FluidStack.loadFluidStackFromNBT(tagCompound.getCompound(FLUID_NBT_KEY));
    }

    @Override
    protected void setFluid(FluidStack fluid) {
        if (!container.hasTag()) {
            container.setTag(new CompoundTag());
        }
        CompoundTag tagCompound = container.getTag();
        if (!tagCompound.contains(NbtTags.BLOCK_ENTITY_NBT_KEY)) {
            container.getTag().put(NbtTags.BLOCK_ENTITY_NBT_KEY, new CompoundTag());
        }
        tagCompound = tagCompound.getCompound(NbtTags.BLOCK_ENTITY_NBT_KEY);

        CompoundTag fluidTag = new CompoundTag();
        fluid.writeToNBT(fluidTag);
        tagCompound.put(FLUID_NBT_KEY, fluidTag);
    }

    @Override
    protected void setContainerToEmpty() {
        CompoundTag tag = container.getTag();
        if (tag != null && tag.contains(NbtTags.BLOCK_ENTITY_NBT_KEY)) {
            tag.getCompound(NbtTags.BLOCK_ENTITY_NBT_KEY).remove(FLUID_NBT_KEY);
        }
    }
}
