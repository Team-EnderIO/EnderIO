package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.client.renderer.blockentity.GraveRenderer;
import com.enderio.base.common.blockentity.GraveBlockEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class EIOBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<GraveBlockEntity> GRAVE = REGISTRATE
        .blockEntity("grave", GraveBlockEntity::new)
        .validBlock(EIOBlocks.GRAVE)
        .renderer(() -> GraveRenderer::new)
        .register();

    public static void register() {}
}
