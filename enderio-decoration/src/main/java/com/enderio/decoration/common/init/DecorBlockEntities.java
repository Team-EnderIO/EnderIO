package com.enderio.decoration.common.init;

import com.enderio.decoration.EIODecor;
import com.enderio.decoration.common.blockentity.DoublePaintedBlockEntity;
import com.enderio.decoration.common.blockentity.LightNodeBlockEntity;
import com.enderio.decoration.common.blockentity.PoweredLightBlockEntity;
import com.enderio.decoration.common.blockentity.SinglePaintedBlockEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

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
    
    public static final BlockEntityEntry<PoweredLightBlockEntity> POWERED_LIGHT = REGISTRATE
    	.blockEntity("powered_light", PoweredLightBlockEntity::new)
    	.validBlocks(DecorBlocks.POWERED_LIGHT, DecorBlocks.POWERED_LIGHT_INVERTED, DecorBlocks.POWERED_LIGHT_WIRELESS, DecorBlocks.POWERED_LIGHT_INVERTED_WIRELESS)
    	.register();
    
    public static final BlockEntityEntry<LightNodeBlockEntity> LIGHT_NODE = REGISTRATE
        	.blockEntity("light_node", LightNodeBlockEntity::new)
        	.validBlock(DecorBlocks.LIGHT_NODE)
        	.register();

    public static void classload() {}

}
