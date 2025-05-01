package com.enderio.machines.common.tag;

import com.enderio.base.api.EnderIO;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;

public class MachineTags {

    public static void register() {
        EntityTypes.init();
        Blocks.init();
    }

    public static class EntityTypes {

        private static void init() {}

        public static TagKey<EntityType<?>> SPAWNER_BLACKLIST = create("spawner_blacklist");

        private static TagKey<EntityType<?>> create(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, EnderIO.loc(pName));
        }
    }

    public static class Blocks {

        private static void init() {}

        public static TagKey<Block> RANGE_EXTENDER = create("range_extender");

        private static TagKey<Block> create(String pName) {
            return TagKey.create(Registries.BLOCK, EnderIO.loc(pName));
        }

    }
}
