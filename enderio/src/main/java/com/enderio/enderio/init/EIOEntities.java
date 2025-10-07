package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.client.content.paint.PaintedSandRenderer;
import com.enderio.enderio.content.paint.PaintedSandEntity;
import com.enderio.regilite.holder.RegiliteEntity;
import com.enderio.regilite.registry.EntityRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIOEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, EnderIO.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<PaintedSandEntity>> PAINTED_SAND = ENTITY_TYPES.register("painted_sand",
        rl -> EntityType.Builder.<PaintedSandEntity>of(PaintedSandEntity::new, MobCategory.MISC).build(rl.getPath()));


    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
