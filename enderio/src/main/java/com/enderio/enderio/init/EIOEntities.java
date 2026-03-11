package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.paint.PaintedSandEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class EIOEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, EnderIO.MOD_ID);

    public static final RegistryObject<EntityType<PaintedSandEntity>> PAINTED_SAND = ENTITY_TYPES.register("painted_sand",
        rl -> EntityType.Builder.<PaintedSandEntity>of(PaintedSandEntity::new, MobCategory.MISC).build(rl.getPath()));


    public static void register(IEventBus bus) {
        ENTITY_TYPES.register(bus);
    }
}
