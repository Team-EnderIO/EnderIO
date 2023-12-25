package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.blockentity.EnderSkullBlockEntity;
import com.enderio.base.common.blockentity.LightNodeBlockEntity;
import com.enderio.base.common.blockentity.PoweredLightBlockEntity;
import com.enderio.base.common.blockentity.SinglePaintedBlockEntity;
import com.enderio.core.common.registry.EnderBlockEntityRegistry;
import com.enderio.core.common.registry.EnderDeferredBlockEntity;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

public class EIOBlockEntities {
    private static final EnderBlockEntityRegistry BLOCK_ENTITIES = EnderBlockEntityRegistry.create(EnderIO.MODID);

    public static final EnderDeferredBlockEntity<SinglePaintedBlockEntity> SINGLE_PAINTED = BLOCK_ENTITIES
        .registerBlockEntity("single_painted", SinglePaintedBlockEntity::new, EIOBlocks.PAINTED_FENCE,
            EIOBlocks.PAINTED_FENCE_GATE,
            EIOBlocks.PAINTED_SAND,
            EIOBlocks.PAINTED_STAIRS,
            EIOBlocks.PAINTED_CRAFTING_TABLE,
            EIOBlocks.PAINTED_REDSTONE_BLOCK,
            EIOBlocks.PAINTED_TRAPDOOR,
            EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE,
            EIOBlocks.PAINTED_GLOWSTONE);

    public static final EnderDeferredBlockEntity<DoublePaintedBlockEntity> DOUBLE_PAINTED = BLOCK_ENTITIES
        .registerBlockEntity("double_painted", DoublePaintedBlockEntity::new, EIOBlocks.PAINTED_SLAB);

    public static final EnderDeferredBlockEntity<PoweredLightBlockEntity> POWERED_LIGHT = BLOCK_ENTITIES
        .registerBlockEntity("powered_light", PoweredLightBlockEntity::new, EIOBlocks.POWERED_LIGHT,
            EIOBlocks.POWERED_LIGHT_INVERTED,
            EIOBlocks.POWERED_LIGHT_WIRELESS,
            EIOBlocks.POWERED_LIGHT_INVERTED_WIRELESS);

    public static final EnderDeferredBlockEntity<LightNodeBlockEntity> LIGHT_NODE = BLOCK_ENTITIES
        .registerBlockEntity("light_node", LightNodeBlockEntity::new, EIOBlocks.LIGHT_NODE);

    public static final EnderDeferredBlockEntity<EnderSkullBlockEntity> ENDER_SKULL = BLOCK_ENTITIES
        .registerBlockEntity("ender_skull", EnderSkullBlockEntity::new, EIOBlocks.WALL_ENDERMAN_HEAD,
            EIOBlocks.ENDERMAN_HEAD);
    public static void register() {
        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
