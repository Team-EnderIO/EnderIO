package com.enderio.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.conduits.common.conduit.type.energy.EnergyConduit;
import com.enderio.enderio.conduits.common.conduit.type.fluid.FluidConduit;
import com.enderio.enderio.conduits.common.conduit.type.item.ItemConduit;
import com.enderio.enderio.conduits.common.conduit.type.redstone.RedstoneConduit;
import net.minecraft.data.worldgen.BootstrapContext;
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
