package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.resources.ResourceKey;

public class EIOConduits {

    // TODO(1.21.1+): change resource keys to match the names
    public static final ResourceKey<Conduit<?, ?>> ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("energy"));
    public static final ResourceKey<Conduit<?, ?>> ENERGETIC_ENERGY = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIO.rl("enhanced_energy"));
    public static final ResourceKey<Conduit<?, ?>> VIBRANT_ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("ender_energy"));
    public static final ResourceKey<Conduit<?, ?>> REDSTONE = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("redstone"));
    public static final ResourceKey<Conduit<?, ?>> FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("fluid"));
    public static final ResourceKey<Conduit<?, ?>> ENERGETIC_FLUID = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIO.rl("pressurized_fluid"));
    public static final ResourceKey<Conduit<?, ?>> VIBRANT_FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("ender_fluid"));
    public static final ResourceKey<Conduit<?, ?>> ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("item"));
    public static final ResourceKey<Conduit<?, ?>> ENERGETIC_ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("enhanced_item"));
    public static final ResourceKey<Conduit<?, ?>> VIBRANT_ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("ender_item"));
}
