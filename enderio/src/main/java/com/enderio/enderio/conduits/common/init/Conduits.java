package com.enderio.enderio.conduits.common.init;

import com.enderio.enderio.api.EnderIOAPI;
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
            EnderIOAPI.loc("energy"));
    public static final ResourceKey<Conduit<?, ?>> ENHANCED_ENERGY = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIOAPI.loc("enhanced_energy"));
    public static final ResourceKey<Conduit<?, ?>> ENDER_ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIOAPI.loc("ender_energy"));
    public static final ResourceKey<Conduit<?, ?>> REDSTONE = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIOAPI.loc("redstone"));
    public static final ResourceKey<Conduit<?, ?>> FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIOAPI.loc("fluid"));
    public static final ResourceKey<Conduit<?, ?>> PRESSURIZED_FLUID = ResourceKey
            .create(EnderIORegistries.Keys.CONDUIT, EnderIOAPI.loc("pressurized_fluid"));
    public static final ResourceKey<Conduit<?, ?>> ENDER_FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIOAPI.loc("ender_fluid"));
    public static final ResourceKey<Conduit<?, ?>> ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIOAPI.loc("item"));
    public static final ResourceKey<Conduit<?, ?>> ENHANCED_ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIOAPI.loc("enhanced_item"));
    public static final ResourceKey<Conduit<?, ?>> ENDER_ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
            EnderIOAPI.loc("ender_item"));

    public static void bootstrap(BootstrapContext<Conduit<?, ?>> context) {
        // TODO: These rates are still up for change, but will refine through testing.
        context.register(ENERGY,
                new EnergyConduit(EnderIOAPI.loc("block/conduit/energy"), ConduitLang.ENERGY_CONDUIT, 6000));
        context.register(ENHANCED_ENERGY, new EnergyConduit(EnderIOAPI.loc("block/conduit/enhanced_energy"),
                ConduitLang.ENHANCED_ENERGY_CONDUIT, 48_000));
        context.register(ENDER_ENERGY,
                new EnergyConduit(EnderIOAPI.loc("block/conduit/ender_energy"), ConduitLang.ENDER_ENERGY_CONDUIT, 384_000));

        context.register(REDSTONE, new RedstoneConduit(EnderIOAPI.loc("block/conduit/redstone"),
                EnderIOAPI.loc("block/conduit/redstone_active"), ConduitLang.REDSTONE_CONDUIT));

        // Fluid conduits tick every 5 ticks, so remember the transfer rate per tick will be *5 for each operation.
        context.register(FLUID,
                new FluidConduit(EnderIOAPI.loc("block/conduit/fluid"), ConduitLang.FLUID_CONDUIT, 200, false, false));
        context.register(PRESSURIZED_FLUID, new FluidConduit(EnderIOAPI.loc("block/conduit/pressurized_fluid"),
                ConduitLang.PRESSURIZED_FLUID_CONDUIT, 1_000, false, true));
        context.register(ENDER_FLUID,
                new FluidConduit(EnderIOAPI.loc("block/conduit/ender_fluid"), ConduitLang.ENDER_FLUID_CONDUIT, 8_000, true, true));

        context.register(ITEM, new ItemConduit(EnderIOAPI.loc("block/conduit/item"), ConduitLang.ITEM_CONDUIT, 32, 20));
        context.register(ENHANCED_ITEM,
                new ItemConduit(EnderIOAPI.loc("block/conduit/item"), ConduitLang.ENHANCED_ITEM_CONDUIT, 64, 20));
        context.register(ENDER_ITEM,
                new ItemConduit(EnderIOAPI.loc("block/conduit/item"), ConduitLang.ENDER_ITEM_CONDUIT, 64, 10));
    }

    public static void register() {
    }
}
