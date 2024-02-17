package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.api.attachment.StoredEntityData;
import com.enderio.api.attachment.CoordinateSelection;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EIOAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EnderIO.MODID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<StoredEntityData>> STORED_ENTITY
        = ATTACHMENT_TYPES.register("stored_entity", () -> AttachmentType.serializable(StoredEntityData::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<CoordinateSelection>> COORDINATE_SELECTION
        = ATTACHMENT_TYPES.register("coordinate_selection", () -> AttachmentType.serializable(CoordinateSelection::new).build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
