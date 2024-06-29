package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import net.minecraft.tags.TagKey;

public class ConduitTags {
    public static void register() {
        ConduitTypes.init();
    }

    public static class ConduitTypes {
        private static void init() {}

        public static final TagKey<ConduitType<?, ?, ?>> ITEM = tag("item");
        public static final TagKey<ConduitType<?, ?, ?>> FLUID = tag("fluid");
        public static final TagKey<ConduitType<?, ?, ?>> ENERGY = tag("energy");
        public static final TagKey<ConduitType<?, ?, ?>> REDSTONE = tag("redstone");

        private static TagKey<ConduitType<?, ?, ?>> tag(String name) {
            return TagKey.create(EnderIORegistries.Keys.CONDUIT_TYPES, EnderIO.loc(name));
        }
    }
}
