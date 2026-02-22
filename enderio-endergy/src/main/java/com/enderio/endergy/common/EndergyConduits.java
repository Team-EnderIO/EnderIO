package com.enderio.endergy.common;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.resources.ResourceKey;

public class EndergyConduits {
    public static final ResourceKey<Conduit<?, ?>> CRUDE_ENERGY = create("crude_energy");
    public static final ResourceKey<Conduit<?, ?>> COPPER_ENERGY = create("copper_energy");
    public static final ResourceKey<Conduit<?, ?>> IRON_ENERGY = create("iron_energy");
    public static final ResourceKey<Conduit<?, ?>> GOLD_ENERGY = create("gold_energy");
    public static final ResourceKey<Conduit<?, ?>> CRYSTALLINE_ENERGY = create("crystalline_energy");
    public static final ResourceKey<Conduit<?, ?>> MELODIC_ENERGY = create("melodic_energy");
    public static final ResourceKey<Conduit<?, ?>> STELLAR_ENERGY = create("stellar_energy");

    private static ResourceKey<Conduit<?, ?>> create(String name) {
        return ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIOEndergy.rl(name));
    }
}
