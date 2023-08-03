package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.blockentity.*;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class EIOBlockEntities {
    private static final Registrate REGISTRATE = EnderIO.registrate();

    public static final BlockEntityEntry<SinglePaintedBlockEntity> SINGLE_PAINTED = REGISTRATE
        .blockEntity("single_painted", SinglePaintedBlockEntity::new)
        .validBlocks(
            EIOBlocks.PAINTED_FENCE,
            EIOBlocks.PAINTED_FENCE_GATE,
            EIOBlocks.PAINTED_SAND,
            EIOBlocks.PAINTED_STAIRS,
            EIOBlocks.PAINTED_CRAFTING_TABLE,
            EIOBlocks.PAINTED_REDSTONE_BLOCK,
            EIOBlocks.PAINTED_TRAPDOOR,
            EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE,
            EIOBlocks.PAINTED_GLOWSTONE
        )
        .register();

    public static final BlockEntityEntry<DoublePaintedBlockEntity> DOUBLE_PAINTED = REGISTRATE
        .blockEntity("double_painted", DoublePaintedBlockEntity::new)
        .validBlocks(EIOBlocks.PAINTED_SLAB)
        .register();

    public static final BlockEntityEntry<PoweredLightBlockEntity> POWERED_LIGHT = REGISTRATE
        .blockEntity("powered_light", PoweredLightBlockEntity::new)
        .validBlocks(EIOBlocks.POWERED_LIGHT, EIOBlocks.POWERED_LIGHT_INVERTED, EIOBlocks.POWERED_LIGHT_WIRELESS, EIOBlocks.POWERED_LIGHT_INVERTED_WIRELESS)
        .register();

    public static final BlockEntityEntry<LightNodeBlockEntity> LIGHT_NODE = REGISTRATE
        .blockEntity("light_node", LightNodeBlockEntity::new)
        .validBlock(EIOBlocks.LIGHT_NODE)
        .register();

    public static final BlockEntityEntry<EnderSkullBlockEntity> ENDER_SKULL = REGISTRATE
        .blockEntity("ender_skull", EnderSkullBlockEntity::new)
        .validBlocks(EIOBlocks.WALL_ENDERMAN_HEAD, EIOBlocks.ENDERMAN_HEAD)
        .register();
    public static void register() {}
}
