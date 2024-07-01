package com.enderio.api.registry;

import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitNetworkContextSerializer;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitTypeSerializer;
import com.enderio.api.conduit.NewConduitTypeSerializer;
import com.enderio.api.travel.TravelTargetSerializer;
import com.enderio.api.travel.TravelTargetType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class EnderIORegistries {

    public static final Registry<TravelTargetType<?>> TRAVEL_TARGET_TYPES = new RegistryBuilder<>(Keys.TRAVEL_TARGET_TYPES)
        .sync(true)
        .create();

    public static final Registry<TravelTargetSerializer<?>> TRAVEL_TARGET_SERIALIZERS = new RegistryBuilder<>(Keys.TRAVEL_TARGET_SERIALIZERS)
        .sync(true)
        .create();

    public static final Registry<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = new RegistryBuilder<>(Keys.CONDUIT_DATA_SERIALIZERS)
        .sync(true)
        .create();

    public static final Registry<ConduitNetworkContextSerializer<?>> CONDUIT_NETWORK_CONTEXT_SERIALIZERS = new RegistryBuilder<>(Keys.CONDUIT_NETWORK_CONTEXT_SERIALIZERS)
        .sync(true)
        .create();

    public static final Registry<NewConduitTypeSerializer<?>> CONDUIT_TYPE_SERIALIZERS = new RegistryBuilder<>(Keys.CONDUIT_TYPE_SERIALIZERS)
        .sync(true)
        .create();

    public static class Keys {
        public static final ResourceKey<Registry<TravelTargetType<?>>> TRAVEL_TARGET_TYPES = createKey("travel_target_types");
        public static final ResourceKey<Registry<TravelTargetSerializer<?>>> TRAVEL_TARGET_SERIALIZERS = createKey("travel_target_serializers");

        public static final ResourceKey<Registry<ConduitDataSerializer<?>>> CONDUIT_DATA_SERIALIZERS = createKey("conduit_data_serializers");
        public static final ResourceKey<Registry<NewConduitTypeSerializer<?>>> CONDUIT_TYPE_SERIALIZERS = createKey("conduit_type_serializers");
        public static final ResourceKey<Registry<ConduitNetworkContextSerializer<?>>> CONDUIT_NETWORK_CONTEXT_SERIALIZERS = createKey("conduit_network_context_serializers");

        /**
         * Conduit types are now a datapack registry.
         */
        public static final ResourceKey<Registry<ConduitType<?, ?, ?>>> CONDUIT_TYPES = createKey("conduit_types");

        private static <T> ResourceKey<Registry<T>> createKey(String name) {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("enderio", name));
        }
    }
}
