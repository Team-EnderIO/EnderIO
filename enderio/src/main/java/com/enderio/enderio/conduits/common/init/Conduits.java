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

    public static void bootstrap(BootstrapContext<Conduit<?, ?>> context) {
        // TODO: These rates are still up for change, but will refine through testing.
        context.register(ENERGY,
                new EnergyConduit(EnderIO.rl("block/conduit/energy"), ConduitLang.ENERGY_CONDUIT, 6000));
        context.register(ENHANCED_ENERGY, new EnergyConduit(EnderIO.rl("block/conduit/enhanced_energy"),
                ConduitLang.ENHANCED_ENERGY_CONDUIT, 48_000));
        context.register(ENDER_ENERGY,
                new EnergyConduit(EnderIO.rl("block/conduit/ender_energy"), ConduitLang.ENDER_ENERGY_CONDUIT, 384_000));

        context.register(REDSTONE, new RedstoneConduit(EnderIO.rl("block/conduit/redstone"),
                EnderIO.rl("block/conduit/redstone_active"), ConduitLang.REDSTONE_CONDUIT));

        // Fluid conduits tick every 5 ticks, so remember the transfer rate per tick will be *5 for each operation.
        context.register(FLUID,
                new FluidConduit(EnderIO.rl("block/conduit/fluid"), ConduitLang.FLUID_CONDUIT, 200, false, false));
        context.register(PRESSURIZED_FLUID, new FluidConduit(EnderIO.rl("block/conduit/pressurized_fluid"),
                ConduitLang.PRESSURIZED_FLUID_CONDUIT, 1_000, false, true));
        context.register(ENDER_FLUID,
                new FluidConduit(EnderIO.rl("block/conduit/ender_fluid"), ConduitLang.ENDER_FLUID_CONDUIT, 8_000, true, true));

        context.register(ITEM, new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ITEM_CONDUIT, 32, 20));
        context.register(ENHANCED_ITEM,
                new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ENHANCED_ITEM_CONDUIT, 64, 20));
        context.register(ENDER_ITEM,
                new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ENDER_ITEM_CONDUIT, 64, 10));
    }

    public static void register() {
    }
}
