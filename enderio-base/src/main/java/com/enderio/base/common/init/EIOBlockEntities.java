package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.common.blockentity.EnderSkullBlockEntity;
import com.enderio.base.common.blockentity.LightNodeBlockEntity;
import com.enderio.base.common.blockentity.PoweredLightBlockEntity;
import com.enderio.base.common.paint.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.paint.blockentity.SinglePaintedBlockEntity;
import com.enderio.regilite.blockentities.RegiliteBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Supplier;

public class EIOBlockEntities {
    private static final RegiliteBlockEntities BLOCK_ENTITIES = EnderIOBase.REGILITE.blockEntities();

    public static final Supplier<BlockEntityType<SinglePaintedBlockEntity>> SINGLE_PAINTED = BLOCK_ENTITIES
        .create("single_painted",
            SinglePaintedBlockEntity::new,
            EIOBlocks.PAINTED_FENCE,
            EIOBlocks.PAINTED_FENCE_GATE,
            EIOBlocks.PAINTED_SAND,
            EIOBlocks.PAINTED_STAIRS,
            EIOBlocks.PAINTED_CRAFTING_TABLE,
            EIOBlocks.PAINTED_REDSTONE_BLOCK,
            EIOBlocks.PAINTED_TRAPDOOR,
            EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE,
            EIOBlocks.PAINTED_GLOWSTONE,
            EIOBlocks.PAINTED_WALL).finish();

    public static final Supplier<BlockEntityType<DoublePaintedBlockEntity>> DOUBLE_PAINTED = BLOCK_ENTITIES
        .create("double_painted", DoublePaintedBlockEntity::new, EIOBlocks.PAINTED_SLAB).finish();

    public static final Supplier<BlockEntityType<PoweredLightBlockEntity>> POWERED_LIGHT = BLOCK_ENTITIES
        .create("powered_light",
            PoweredLightBlockEntity::new,
            EIOBlocks.POWERED_LIGHT,
            EIOBlocks.POWERED_LIGHT_INVERTED,
            EIOBlocks.POWERED_LIGHT_WIRELESS,
            EIOBlocks.POWERED_LIGHT_INVERTED_WIRELESS)
        .finish();

    public static final Supplier<BlockEntityType<LightNodeBlockEntity>> LIGHT_NODE = BLOCK_ENTITIES
        .create("light_node", LightNodeBlockEntity::new, EIOBlocks.LIGHT_NODE).finish();

    public static final Supplier<BlockEntityType<EnderSkullBlockEntity>> ENDER_SKULL = BLOCK_ENTITIES
        .create("ender_skull", EnderSkullBlockEntity::new, EIOBlocks.WALL_ENDERMAN_HEAD, EIOBlocks.ENDERMAN_HEAD).finish();

    public static void register() {
    }
}
