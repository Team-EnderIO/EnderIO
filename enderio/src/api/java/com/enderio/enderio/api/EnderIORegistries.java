package com.enderio.enderio.api;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.ConnectionConfigType;
import com.enderio.enderio.api.conduits.network.ConduitNetworkContextType;
import com.enderio.enderio.api.conduits.network.node.NodeDataType;
import com.enderio.enderio.api.conduits.network.node.legacy.ConduitDataType;
import com.enderio.enderio.api.farm.FarmTask;
import com.enderio.enderio.api.travel.TravelTargetSerializer;
import com.enderio.enderio.api.travel.TravelTargetType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.ApiStatus;

public class EnderIORegistries {

    public static final Registry<TravelTargetType<?>> TRAVEL_TARGET_TYPES = new RegistryBuilder<>(Keys.TRAVEL_TARGET_TYPES)
        .sync(true)
        .create();

    public static final Registry<TravelTargetSerializer<?>> TRAVEL_TARGET_SERIALIZERS = new RegistryBuilder<>(Keys.TRAVEL_TARGET_SERIALIZERS)
        .sync(true)
        .create();

    public static final Registry<ConduitType<?>> CONDUIT_TYPE = new RegistryBuilder<>(Keys.CONDUIT_TYPE).sync(true)
            .create();

    @Deprecated(since = "8.0.0")
    public static final Registry<ConduitDataType<?>> CONDUIT_DATA_TYPE = new RegistryBuilder<>(Keys.CONDUIT_DATA_TYPE)
            .sync(true)
            .create();

    public static final Registry<NodeDataType<?>> CONDUIT_NODE_DATA_TYPE = new RegistryBuilder<>(
            Keys.CONDUIT_NODE_DATA_TYPE).sync(true).create();

    public static final Registry<ConnectionConfigType<?>> CONDUIT_CONNECTION_CONFIG_TYPE = new RegistryBuilder<>(
            Keys.CONDUIT_CONNECTION_CONFIG_TYPE).sync(true).create();

    public static final Registry<ConduitNetworkContextType<?>> CONDUIT_NETWORK_CONTEXT_TYPE = new RegistryBuilder<>(
            Keys.CONDUIT_NETWORK_CONTEXT_TYPE).sync(true).create();

    public static class Keys {
        public static final ResourceKey<Registry<TravelTargetType<?>>> TRAVEL_TARGET_TYPES = createKey("travel_target_types");
        public static final ResourceKey<Registry<TravelTargetSerializer<?>>> TRAVEL_TARGET_SERIALIZERS = createKey("travel_target_serializers");

        @Deprecated(since = "8.0.0")
        public static final ResourceKey<Registry<ConduitDataType<?>>> CONDUIT_DATA_TYPE = createKey(
                "conduit_data_type");

        public static final ResourceKey<Registry<NodeDataType<?>>> CONDUIT_NODE_DATA_TYPE = createKey(
                "conduit_node_data_type");
        public static final ResourceKey<Registry<ConnectionConfigType<?>>> CONDUIT_CONNECTION_CONFIG_TYPE = createKey(
                "conduit_connection_config_type");
        public static final ResourceKey<Registry<ConduitNetworkContextType<?>>> CONDUIT_NETWORK_CONTEXT_TYPE = createKey(
                "conduit_network_context_type");

        public static final ResourceKey<Registry<ConduitType<?>>> CONDUIT_TYPE = createKey("conduit_type");

        /**
         * Conduit types are now a datapack registry.
         */
        public static final ResourceKey<Registry<Conduit<?, ?>>> CONDUIT = createKey("conduit");

        private static <T> ResourceKey<Registry<T>> createKey(String name) {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("enderio", name));
        }
    }
}
