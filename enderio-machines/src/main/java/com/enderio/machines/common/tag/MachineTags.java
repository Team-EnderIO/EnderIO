package com.enderio.machines.common.tag;

import com.enderio.EnderIOBase;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

public class MachineTags {

    public static void register() {
        ItemTags.init();
        EntityTypes.init();
    }

    public static class ItemTags {

        private static void init() {}

        public static TagKey<Item> EXPLOSIVES = create("explosives");
        public static TagKey<Item> REDSTONE_SOURCE = create("redstone_source");

        private static TagKey<Item> create(String pName) {
            return TagKey.create(Registries.ITEM, EnderIOBase.loc(pName));
        }

    }

    public static class EntityTypes {

        private static void init() {}

        public static TagKey<EntityType<?>> SPAWNER_BLACKLIST = create("spawner_blacklist");

        private static TagKey<EntityType<?>> create(String pName) {
            return TagKey.create(Registries.ENTITY_TYPE, EnderIOBase.loc(pName));
        }
    }
}
