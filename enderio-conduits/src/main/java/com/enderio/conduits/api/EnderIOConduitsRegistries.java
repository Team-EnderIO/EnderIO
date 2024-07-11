package com.enderio.conduits.api;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitDataType;
import com.enderio.conduits.api.ConduitNetworkContextType;
import com.enderio.conduits.api.ConduitType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class EnderIOConduitsRegistries {

    public static final Registry<ConduitType<?>> CONDUIT_TYPE = new RegistryBuilder<>(Keys.CONDUIT_TYPE)
        .sync(true)
        .create();

    public static final Registry<ConduitDataType<?>> CONDUIT_DATA_TYPE = new RegistryBuilder<>(Keys.CONDUIT_DATA_TYPE)
        .sync(true)
        .create();

    public static final Registry<ConduitNetworkContextType<?>> CONDUIT_NETWORK_CONTEXT_TYPE = new RegistryBuilder<>(Keys.CONDUIT_NETWORK_CONTEXT_TYPE)
        .sync(true)
        .create();

    public static class Keys {
        public static final ResourceKey<Registry<ConduitDataType<?>>> CONDUIT_DATA_TYPE = createKey("conduit_data_type");
        public static final ResourceKey<Registry<ConduitNetworkContextType<?>>> CONDUIT_NETWORK_CONTEXT_TYPE = createKey("conduit_network_context_type");

        public static final ResourceKey<Registry<ConduitType<?>>> CONDUIT_TYPE = createKey("conduit_type");

        /**
         * Conduit types are now a datapack registry.
         */
        public static final ResourceKey<Registry<Conduit<?>>> CONDUIT = createKey("conduit");

        private static <T> ResourceKey<Registry<T>> createKey(String name) {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("enderio", name));
        }
    }
}
