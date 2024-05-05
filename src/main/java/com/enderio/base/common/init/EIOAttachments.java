package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.core.common.attachment.AttachmentUtil;
import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EIOAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EnderIO.MODID);

    public static final Supplier<AttachmentType<FluidHandlerItemStack>> ITEM_SIMPLE_FLUID
        = ATTACHMENT_TYPES.register("item_simple_fluid", AttachmentUtil.itemFluidHandlerAttachment());

    public static final Supplier<AttachmentType<StrictFluidHandlerItemStack>> ITEM_STRICT_FLUID
        = ATTACHMENT_TYPES.register("item_strict_fluid", AttachmentUtil.strictItemFluidHandlerAttachment());

    public static final Supplier<AttachmentType<EnergyStorage>> ITEM_ENERGY_STORAGE
        = ATTACHMENT_TYPES.register("item_energy_storage", AttachmentUtil.itemEnergyStorageAttachment());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
