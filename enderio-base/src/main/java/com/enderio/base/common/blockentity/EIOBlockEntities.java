package com.enderio.base.common.blockentity;

import com.enderio.base.EnderIO;
import com.enderio.base.common.block.EIOBlocks;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EIOBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<BlockEntity> GRAVE = REGISTRATE
        .blockEntity("grave", (t, p, s) -> new GraveBlockEntity(t, p, s))
        .validBlock(EIOBlocks.GRAVE)
        .register();

    public static void register() {}
}
