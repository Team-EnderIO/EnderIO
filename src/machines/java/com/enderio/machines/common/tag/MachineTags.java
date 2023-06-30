package com.enderio.machines.common.tag;

import com.enderio.EnderIO;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class MachineTags {

    public static void register() {
        EntityTypes.init();
    }

    public static class EntityTypes {

        private static void init() {}

        public static TagKey<EntityType<?>> SPAWNER_BLACKLIST = create("spawner_blacklist");

        private static TagKey<EntityType<?>> create(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, EnderIO.loc(pName));
        }
    }
}
