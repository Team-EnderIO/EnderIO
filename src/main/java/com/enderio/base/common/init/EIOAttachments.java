package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.paint.BlockPaintData;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EIOAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EnderIO.MODID);

    // TODO: Do we use these or nah?
    public static final Supplier<AttachmentType<BlockPaintData>> PRIMARY_PAINT =
        ATTACHMENT_TYPES.register("primary_paint", () ->
            AttachmentType.builder(() -> BlockPaintData.of(Blocks.AIR))
                .serialize(BlockPaintData.CODEC)
                .build());

    public static final Supplier<AttachmentType<BlockPaintData>> SECONDARY_PAINT =
        ATTACHMENT_TYPES.register("secondary_paint", () ->
            AttachmentType.builder(() -> BlockPaintData.of(Blocks.AIR))
                .serialize(BlockPaintData.CODEC)
                .build());

    public static void register(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
