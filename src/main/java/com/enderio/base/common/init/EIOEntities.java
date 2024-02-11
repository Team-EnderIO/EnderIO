package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.entity.PaintedSandEntity;
import com.enderio.regilite.holder.RegiliteEntity;
import com.enderio.regilite.registry.EntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;

public class EIOEntities {

    private static final EntityRegistry ENTITY_REGISTRY = EntityRegistry.create(EnderIO.MODID);

    public static final RegiliteEntity<PaintedSandEntity> PAINTED_SAND = ENTITY_REGISTRY
        .registerEntity("painted_sand", (EntityType.EntityFactory<PaintedSandEntity>) PaintedSandEntity::new, MobCategory.MISC)
        // TODO: NEO-PORT: Regilite needs entity renderers
        //.renderer(() -> PaintedSandRenderer::new)
        .setTranslation("Painted Sand");

    public static void register(IEventBus bus) {
        ENTITY_REGISTRY.register(bus);
    }
}
