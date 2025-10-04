package com.enderio.endergy.common;

import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import net.minecraft.resources.ResourceKey;

public class EndergyConduits {
    public static final ResourceKey<Conduit<?, ?>> CRYSTALLINE_ENERGY = create("crystalline_energy");
    public static final ResourceKey<Conduit<?, ?>> CRYSTALLINE_PINK_SLIME_ENERGY = create("crystalline_pink_slime_energy");
    public static final ResourceKey<Conduit<?, ?>> MELODIC_ENERGY = create("melodic_energy");
    public static final ResourceKey<Conduit<?, ?>> STELLAR_ENERGY = create("stellar_energy");

    private static ResourceKey<Conduit<?, ?>> create(String name) {
        return ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIOEndergy.rl(name));
    }
}
