package com.enderio.core.common.attachment;

import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.apache.commons.lang3.NotImplementedException;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class AttachmentUtil {
    // TODO: 1.20.6: Change to data components.

    public static Supplier<AttachmentType<FluidHandlerItemStack>> itemFluidHandlerAttachment() {
        return () -> AttachmentType.builder(holder -> {
            return (FluidHandlerItemStack)null;
        }).build();
    }

    public static Supplier<AttachmentType<StrictFluidHandlerItemStack>> strictItemFluidHandlerAttachment() {
        return () -> AttachmentType.builder(holder -> {
            return (StrictFluidHandlerItemStack)null;
        }).build();
    }

    public static Supplier<AttachmentType<EnergyStorage>> itemEnergyStorageAttachment() {
        return () -> AttachmentType.serializable(holder -> {
            return (EnergyStorage)null;
        }).build();
    }
}
