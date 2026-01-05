package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.resources.ResourceKey;

public class EIOConduits {

    public static final ResourceKey<Conduit<?, ?>> ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("energy"));
    public static final ResourceKey<Conduit<?, ?>> ENERGETIC_ENERGY = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIO.id("energetic_energy"));
    public static final ResourceKey<Conduit<?, ?>> VIBRANT_ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("vibrant_energy"));
    public static final ResourceKey<Conduit<?, ?>> REDSTONE = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("redstone"));
    public static final ResourceKey<Conduit<?, ?>> FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("fluid"));
    public static final ResourceKey<Conduit<?, ?>> ENERGETIC_FLUID = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIO.id("energetic_fluid"));
    public static final ResourceKey<Conduit<?, ?>> VIBRANT_FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("vibrant_fluid"));
    public static final ResourceKey<Conduit<?, ?>> ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("item"));
    public static final ResourceKey<Conduit<?, ?>> ENERGETIC_ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("energetic_item"));
    public static final ResourceKey<Conduit<?, ?>> VIBRANT_ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.id("vibrant_item"));
}
