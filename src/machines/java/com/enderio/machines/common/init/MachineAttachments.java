package com.enderio.machines.common.init;

import com.enderio.EnderIO;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.machines.common.attachment.ActionRange;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Locale;
import java.util.function.Supplier;

public class MachineAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, EnderIO.MODID);

    public static final Supplier<AttachmentType<RedstoneControl>> REDSTONE_CONTROL
        = ATTACHMENT_TYPES.register("redstone_control", () -> AttachmentType
        .builder(() -> RedstoneControl.ALWAYS_ACTIVE)
        .serialize(ExtraCodecs.stringResolverCodec(RedstoneControl::getSerializedName, s -> RedstoneControl.valueOf(s.toUpperCase(Locale.ROOT)))).build());

    public static final Supplier<AttachmentType<ActionRange>> ACTION_RANGE
        = ATTACHMENT_TYPES.register("action_range", () -> AttachmentType.builder(() -> new ActionRange(3, false)).serialize(ActionRange.CODEC).build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
