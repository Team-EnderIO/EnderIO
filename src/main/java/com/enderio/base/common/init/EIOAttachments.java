package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.api.attachment.CoordinateSelection;
import com.enderio.api.attachment.StoredEntityData;
import com.enderio.base.common.capacitor.LootCapacitorData;
import com.enderio.core.common.capability.ItemFilterCapability;
import com.enderio.core.common.attachment.AttachmentUtil;
import com.enderio.core.common.capability.StrictFluidHandlerItemStack;
import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.energy.EnergyStorage;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EIOAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EnderIO.MODID);

    public static final Supplier<AttachmentType<StoredEntityData>> STORED_ENTITY
        = ATTACHMENT_TYPES.register("stored_entity", () -> AttachmentType.serializable(StoredEntityData::new).build());

    public static final Supplier<AttachmentType<CoordinateSelection>> COORDINATE_SELECTION
        = ATTACHMENT_TYPES.register("coordinate_selection", () -> AttachmentType.serializable(CoordinateSelection::new).build());

    public static final Supplier<AttachmentType<Boolean>> TOGGLED
        = ATTACHMENT_TYPES.register("toggled", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());

    public static final Supplier<AttachmentType<LootCapacitorData>> LOOT_CAPACITOR_DATA
        = ATTACHMENT_TYPES.register("loot_capacitor_data", () -> AttachmentType.serializable(LootCapacitorData::new).build());

    public static final Supplier<AttachmentType<FluidHandlerItemStack>> ITEM_SIMPLE_FLUID
        = ATTACHMENT_TYPES.register("item_simple_fluid", AttachmentUtil.itemFluidHandlerAttachment());

    public static final Supplier<AttachmentType<StrictFluidHandlerItemStack>> ITEM_STRICT_FLUID
        = ATTACHMENT_TYPES.register("item_strict_fluid", AttachmentUtil.strictItemFluidHandlerAttachment());

    public static final Supplier<AttachmentType<EnergyStorage>> ITEM_ENERGY_STORAGE
        = ATTACHMENT_TYPES.register("item_energy_storage", AttachmentUtil.itemEnergyStorageAttachment());

    public static final Supplier<AttachmentType<ItemFilterCapability>> ITEM_FILTER
        = ATTACHMENT_TYPES.register("item_filter", AttachmentUtil.itemFilterAttachment());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
