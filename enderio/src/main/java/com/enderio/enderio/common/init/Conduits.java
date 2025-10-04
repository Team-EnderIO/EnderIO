package com.enderio.enderio.common.init;

import com.enderio.enderio.common.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.resources.ResourceKey;

public class Conduits {

    public static final ResourceKey<Conduit<?, ?>> ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("energy"));
    public static final ResourceKey<Conduit<?, ?>> ENHANCED_ENERGY = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIO.rl("enhanced_energy"));
    public static final ResourceKey<Conduit<?, ?>> ENDER_ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("ender_energy"));
    public static final ResourceKey<Conduit<?, ?>> REDSTONE = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("redstone"));
    public static final ResourceKey<Conduit<?, ?>> FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("fluid"));
    public static final ResourceKey<Conduit<?, ?>> PRESSURIZED_FLUID = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIO.rl("pressurized_fluid"));
    public static final ResourceKey<Conduit<?, ?>> ENDER_FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("ender_fluid"));
    public static final ResourceKey<Conduit<?, ?>> ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("item"));
    public static final ResourceKey<Conduit<?, ?>> ENHANCED_ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("enhanced_item"));
    public static final ResourceKey<Conduit<?, ?>> ENDER_ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIO.rl("ender_item"));
}
