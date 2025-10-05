package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class EIOAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EnderIO.MOD_ID);

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
