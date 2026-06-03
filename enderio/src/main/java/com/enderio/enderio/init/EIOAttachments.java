package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.fire_crafting.FireCraftingManager;
import com.enderio.enderio.content.machines.obelisks.aversion.AversionObeliskManager;
import com.enderio.enderio.content.machines.obelisks.inhibitor.InhibitorObeliskManager;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EIOAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.ATTACHMENT_TYPES, EnderIO.MOD_ID);

    public static final Supplier<AttachmentType<AversionObeliskManager>> AVERSION_OBELISK_MANAGER = ATTACHMENT_TYPES
            .register("aversion_obelisk_manager", () -> AttachmentType.builder(AversionObeliskManager::new).build());

    public static final Supplier<AttachmentType<InhibitorObeliskManager>> INHIBITOR_OBELISK_MANAGER = ATTACHMENT_TYPES
            .register("inhibitor_obelisk_manager", () -> AttachmentType.builder(InhibitorObeliskManager::new).build());

    public static final Supplier<AttachmentType<RelocatorObeliskManager>> RELOCATOR_OBELISK_MANAGER = ATTACHMENT_TYPES
            .register("relocator_obelisk_manager", () -> AttachmentType.builder(RelocatorObeliskManager::new).build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register("fire_crafting_manager", () -> FireCraftingManager.ATTACHMENT_TYPE);

        ATTACHMENT_TYPES.register(bus);
    }
}
