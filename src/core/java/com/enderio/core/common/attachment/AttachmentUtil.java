package com.enderio.core.common.attachment;

import com.enderio.core.common.capability.ItemFilterCapability;
import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import com.enderio.core.common.item.IEnderFilter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.energy.EnergyStorage;
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
                    capacity = attachedFluidTank.getFluidCapacity();
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
                    capacity = attachedFluidTank.getFluidCapacity();
                    filter = attachedFluidTank.getFluidFilter();
                }

                return new StrictFluidHandlerItemStack(itemStack, capacity, filter);
            } else {
                throw new IllegalStateException("Cannot attach fluid handler item to a non-item.");
            }
        }).build();
    }

    public static Supplier<AttachmentType<EnergyStorage>> itemEnergyStorageAttachment() {
        return () -> AttachmentType.serializable(holder -> {
            if (holder instanceof ItemStack itemStack) {
                int capacity = 1000;
                if (itemStack.getItem() instanceof IEnergyStorageConfig energyStorageConfig) {
                    capacity = energyStorageConfig.getMaxEnergy();
                }

                return new EnergyStorage(capacity);
            } else {
                // TODO: Add block support for machines?
                throw new IllegalStateException("Cannot attach fluid handler item to a non-item.");
            }
        }).build();
    }

    public static Supplier<AttachmentType<ItemFilterCapability>> itemFilterAttachment() {
        return () -> AttachmentType.serializable(holder -> {
            if (holder instanceof ItemStack itemStack) {
                if (itemStack.getItem() instanceof IEnderFilter filer) {
                    return new ItemFilterCapability(filer.size(), false, false);
                }
                return new ItemFilterCapability(0, false, false);
            } else {
                // TODO: Add block support for machines?
                throw new IllegalStateException("Cannot attach item filter handler item to a non-item.");
            }
        }).build();
    }
}
