package com.enderio.api.registry;

import com.enderio.api.conduit.ConduitType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class EnderIORegistries {
    public static class Keys {
        public static final ResourceKey<Registry<ConduitType<?>>> CONDUIT_TYPES = createKey("conduit_types");

        private static <T> ResourceKey<Registry<T>> createKey(String name) {
            return ResourceKey.createRegistryKey(new ResourceLocation("enderio", name));
        }
    }
}
