package com.enderio.base.api.registry;

import com.enderio.base.api.travel.TravelTargetSerializer;
import com.enderio.base.api.travel.TravelTargetType;
import com.enderio.machines.common.soulpot.OriginType;
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
    public static final Registry<OriginType<?>> ORIGIN_TYPES = new RegistryBuilder<>(Keys.ORIGIN_TYPES)
        .sync(true)
        .create();

    public static class Keys {
        public static final ResourceKey<Registry<TravelTargetType<?>>> TRAVEL_TARGET_TYPES = createKey("travel_target_types");
        public static final ResourceKey<Registry<TravelTargetSerializer<?>>> TRAVEL_TARGET_SERIALIZERS = createKey("travel_target_serializers");
        public static final ResourceKey<Registry<OriginType<?>>> ORIGIN_TYPES = createKey("origin_type");

        private static <T> ResourceKey<Registry<T>> createKey(String name) {
            return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("enderio", name));
        }
    }
}
