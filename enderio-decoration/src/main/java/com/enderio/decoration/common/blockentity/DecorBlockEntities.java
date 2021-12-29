package com.enderio.decoration.common.blockentity;

import com.enderio.decoration.EIODecor;
import com.enderio.decoration.common.block.DecorBlocks;
import com.enderio.registrate.Registrate;
import com.enderio.registrate.util.entry.BlockEntityEntry;
import com.enderio.registrate.util.nullness.NonNullSupplier;

public class DecorBlockEntities {

    private static final Registrate REGISTRATE = EIODecor.registrate();

    public static final BlockEntityEntry<SinglePaintedBlockEntity> SINGLE_PAINTED = REGISTRATE
        .blockEntity("single_painted", SinglePaintedBlockEntity::new)
        .validBlocks(DecorBlocks.getPaintedSupplier().toArray(new NonNullSupplier[0]))
        .register();

    public static final BlockEntityEntry<DoublePaintedBlockEntity> DOUBLE_PAINTED = REGISTRATE
        .blockEntity("double_painted", DoublePaintedBlockEntity::new)
        .validBlocks(DecorBlocks.PAINTED_SLAB)
        .register();

    public static void register() {}

}
