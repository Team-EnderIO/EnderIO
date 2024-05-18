package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.paint.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.blockentity.EnderSkullBlockEntity;
import com.enderio.base.common.blockentity.LightNodeBlockEntity;
import com.enderio.base.common.blockentity.PoweredLightBlockEntity;
import com.enderio.base.common.paint.blockentity.SinglePaintedBlockEntity;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockEntityRegistry;
import net.neoforged.bus.api.IEventBus;

public class EIOBlockEntities {
    private static final BlockEntityRegistry BLOCK_ENTITY_REGISTRY = EnderIO.getRegilite().blockEntityRegistry();

    public static final RegiliteBlockEntity<SinglePaintedBlockEntity> SINGLE_PAINTED = BLOCK_ENTITY_REGISTRY
        .registerBlockEntity("single_painted",
            SinglePaintedBlockEntity::new,
            EIOBlocks.PAINTED_FENCE,
            EIOBlocks.PAINTED_FENCE_GATE,
            EIOBlocks.PAINTED_SAND,
            EIOBlocks.PAINTED_STAIRS,
            EIOBlocks.PAINTED_CRAFTING_TABLE,
            EIOBlocks.PAINTED_REDSTONE_BLOCK,
            EIOBlocks.PAINTED_TRAPDOOR,
            EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE,
            EIOBlocks.PAINTED_GLOWSTONE);

    public static final RegiliteBlockEntity<DoublePaintedBlockEntity> DOUBLE_PAINTED = BLOCK_ENTITY_REGISTRY
        .registerBlockEntity("double_painted", DoublePaintedBlockEntity::new, EIOBlocks.PAINTED_SLAB);

    public static final RegiliteBlockEntity<PoweredLightBlockEntity> POWERED_LIGHT = BLOCK_ENTITY_REGISTRY
        .registerBlockEntity("powered_light",
            PoweredLightBlockEntity::new,
            EIOBlocks.POWERED_LIGHT,
            EIOBlocks.POWERED_LIGHT_INVERTED,
            EIOBlocks.POWERED_LIGHT_WIRELESS,
            EIOBlocks.POWERED_LIGHT_INVERTED_WIRELESS);

    public static final RegiliteBlockEntity<LightNodeBlockEntity> LIGHT_NODE = BLOCK_ENTITY_REGISTRY
        .registerBlockEntity("light_node", LightNodeBlockEntity::new, EIOBlocks.LIGHT_NODE);

    public static final RegiliteBlockEntity<EnderSkullBlockEntity> ENDER_SKULL = BLOCK_ENTITY_REGISTRY
        .registerBlockEntity("ender_skull", EnderSkullBlockEntity::new, EIOBlocks.WALL_ENDERMAN_HEAD, EIOBlocks.ENDERMAN_HEAD);

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_REGISTRY.register(eventBus);
    }
}
