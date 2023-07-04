package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class ConduitBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<ConduitBlockEntity> CONDUIT = REGISTRATE
        .blockEntity("conduit", ConduitBlockEntity::new)
        .validBlock(ConduitBlocks.CONDUIT)
        .register();

    public static void register() {}
}
