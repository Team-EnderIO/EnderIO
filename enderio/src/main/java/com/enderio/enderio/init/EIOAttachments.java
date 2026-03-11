package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.machines.obelisks.aversion.AversionObeliskManager;
import com.enderio.enderio.content.machines.obelisks.inhibitor.InhibitorObeliskManager;
import com.enderio.enderio.content.machines.obelisks.relocator.RelocatorObeliskManager;
import com.enderio.enderio.foundation.attachment.ActionRange;
import com.enderio.enderio.foundation.io.IOConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.attachment.AttachmentType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class EIOAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister
            .create(ForgeRegistries.ATTACHMENT_TYPES, EnderIO.MOD_ID);

    @Deprecated(forRemoval = true, since = "7.1")
    public static final Supplier<AttachmentType<RedstoneControl>> REDSTONE_CONTROL = ATTACHMENT_TYPES.register(
            "redstone_control",
            () -> AttachmentType.builder(() -> RedstoneControl.ALWAYS_ACTIVE).serialize(RedstoneControl.CODEC).build());

    @Deprecated(forRemoval = true, since = "7.1")
    public static final Supplier<AttachmentType<ActionRange>> ACTION_RANGE = ATTACHMENT_TYPES.register("action_range",
            () -> AttachmentType.builder(() -> new ActionRange(3, false)).serialize(ActionRange.CODEC).build());

    @Deprecated(forRemoval = true, since = "7.1")
    public static final Supplier<AttachmentType<IOConfig>> IO_CONFIG = ATTACHMENT_TYPES.register("io_config",
            () -> AttachmentType.builder(IOConfig::empty).serialize(IOConfig.CODEC).build());

    public static final Supplier<AttachmentType<AversionObeliskManager>> AVERSION_OBELISK_MANAGER = ATTACHMENT_TYPES
            .register("aversion_obelisk_manager", () -> AttachmentType.builder(AversionObeliskManager::new).build());

    public static final Supplier<AttachmentType<InhibitorObeliskManager>> INHIBITOR_OBELISK_MANAGER = ATTACHMENT_TYPES
            .register("inhibitor_obelisk_manager", () -> AttachmentType.builder(InhibitorObeliskManager::new).build());

    public static final Supplier<AttachmentType<RelocatorObeliskManager>> RELOCATOR_OBELISK_MANAGER = ATTACHMENT_TYPES
            .register("relocator_obelisk_manager", () -> AttachmentType.builder(RelocatorObeliskManager::new).build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
