package com.enderio.core.common.attachment;

import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AttachmentUtil {
    // TODO: NEO-PORT: Would be nice for there to be a variant that doesn't act upon the item's NBT.
    public static Supplier<AttachmentType<FluidHandlerItemStack>> itemFluidHandlerAttachment() {
        return () -> AttachmentType.builder(holder -> {
            if (holder instanceof ItemStack itemStack) {
                int capacity = 1000;
                if (itemStack.getItem() instanceof IItemFluidHandlerConfig attachedFluidTank) {
                    capacity = attachedFluidTank.getCapacity();
                }

                return new FluidHandlerItemStack(itemStack, capacity);
            } else {
                throw new IllegalStateException("Cannot attach fluid handler item to a non-item.");
            }
        }).build();
    }

    public static Supplier<AttachmentType<StrictFluidHandlerItemStack>> strictItemFluidHandlerAttachment() {
        return () -> AttachmentType.builder(holder -> {
            if (holder instanceof ItemStack itemStack) {
                Predicate<Fluid> filter = f -> true;
                int capacity = 1000;
                if (itemStack.getItem() instanceof IStrictItemFluidHandlerConfig attachedFluidTank) {
                    capacity = attachedFluidTank.getCapacity();
                    filter = attachedFluidTank.getFluidFilter();
                }

                return new StrictFluidHandlerItemStack(itemStack, capacity, filter);
            } else {
                throw new IllegalStateException("Cannot attach fluid handler item to a non-item.");
            }
        }).build();
    }
}
