package com.enderio.base.common.init;

import com.enderio.base.EnderIO;
import com.enderio.base.common.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.blockentity.GraveBlockEntity;
import com.enderio.base.common.blockentity.SinglePaintedBlockEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

public class EIOBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<SinglePaintedBlockEntity> SINGLE_PAINTED = REGISTRATE
        .blockEntity("single_painted", SinglePaintedBlockEntity::new)
        .validBlocks(EIOBlocks.getPaintedSupplier().toArray(new NonNullSupplier[0]))
        .register();

    public static final BlockEntityEntry<DoublePaintedBlockEntity> DOUBLE_PAINTED = REGISTRATE
        .blockEntity("double_painted", DoublePaintedBlockEntity::new)
        .validBlocks(EIOBlocks.PAINTED_SLAB)
        .register();

    public static final BlockEntityEntry<GraveBlockEntity> GRAVE = REGISTRATE
        .blockEntity("grave", GraveBlockEntity::new)
        .validBlock(EIOBlocks.GRAVE)
        .register();

    public static void classload() {}
}
