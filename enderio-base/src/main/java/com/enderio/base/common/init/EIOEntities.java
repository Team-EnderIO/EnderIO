package com.enderio.base.common.init;

import com.enderio.EnderIOBase;
import com.enderio.base.client.paint.PaintedSandRenderer;
import com.enderio.base.common.paint.PaintedSandEntity;
import com.enderio.regilite.entities.RegiliteEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

import java.util.function.Supplier;

public class EIOEntities {

    private static final RegiliteEntityTypes ENTITY_TYPES = EnderIOBase.REGILITE.entityTypes();

    public static final Supplier<EntityType<PaintedSandEntity>> PAINTED_SAND = ENTITY_TYPES
        .create("painted_sand", (EntityType.EntityFactory<PaintedSandEntity>) PaintedSandEntity::new, MobCategory.MISC)
        .renderer(() -> PaintedSandRenderer::new)
        .translation("Painted Sand")
        .finish();

    public static void register() {
    }
}
