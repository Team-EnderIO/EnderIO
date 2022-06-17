package com.enderio.base.common.init;

import com.enderio.base.EnderIO;
import com.enderio.base.common.blockentity.GraveBlockEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class EIOBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<GraveBlockEntity> GRAVE = REGISTRATE
        .blockEntity("grave", GraveBlockEntity::new)
        .validBlock(EIOBlocks.GRAVE)
        .register();

    public static void register() {}
}
