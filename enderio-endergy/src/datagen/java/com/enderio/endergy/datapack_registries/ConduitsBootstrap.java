package com.enderio.endergy.datapack_registries;

import com.enderio.endergy.common.EnderIOEndergy;
import com.enderio.endergy.common.EndergyConduits;
import com.enderio.endergy.common.init.EndergyCreativeTabs;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduit;
import net.minecraft.Util;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;

public class ConduitsBootstrap {
    public static void bootstrap(BootstrapContext<Conduit<?, ?>> context) {
        // TODO: Need to adapt rates to new balance, just getting them in
        register(context, EndergyConduits.CRUDE_ENERGY, (desc) -> new EnergyConduit(EnderIOEndergy.rl("block/conduit/crude_energy"), desc,
            Optional.of(EndergyCreativeTabs.MAIN), 20));
        register(context, EndergyConduits.COPPER_ENERGY, (desc) -> new EnergyConduit(EnderIOEndergy.rl("block/conduit/copper_energy"), desc,
            Optional.of(EndergyCreativeTabs.MAIN), 40));
        register(context, EndergyConduits.IRON_ENERGY, (desc) -> new EnergyConduit(EnderIOEndergy.rl("block/conduit/iron_energy"), desc,
            Optional.of(EndergyCreativeTabs.MAIN),80));
        register(context, EndergyConduits.GOLD_ENERGY, (desc) -> new EnergyConduit(EnderIOEndergy.rl("block/conduit/gold_energy"), desc,
            Optional.of(EndergyCreativeTabs.MAIN), 160));
        register(context, EndergyConduits.CRYSTALLINE_ENERGY, (desc) -> new EnergyConduit(EnderIOEndergy.rl("block/conduit/crystalline_energy"), desc,
            Optional.of(EndergyCreativeTabs.MAIN), 384_000));
        register(context, EndergyConduits.MELODIC_ENERGY, (desc) -> new EnergyConduit(EnderIOEndergy.rl("block/conduit/melodic_energy"), desc,
            Optional.of(EndergyCreativeTabs.MAIN), 768_000));
        register(context, EndergyConduits.STELLAR_ENERGY, (desc) -> new EnergyConduit(EnderIOEndergy.rl("block/conduit/stellar_energy"), desc,
            Optional.of(EndergyCreativeTabs.MAIN), Integer.MAX_VALUE));

//        context.register(Conduits.ENHANCED_ENERGY, new EnergyConduit(EnderIO.rl("block/conduit/enhanced_energy"),
//            ConduitLang.ENHANCED_ENERGY_CONDUIT, 48_000));
//        context.register(Conduits.ENDER_ENERGY,
//            new EnergyConduit(EnderIO.rl("block/conduit/ender_energy"), ConduitLang.ENDER_ENERGY_CONDUIT, 384_000));
//
//        context.register(Conduits.REDSTONE, new RedstoneConduit(EnderIO.rl("block/conduit/redstone"),
//            EnderIO.rl("block/conduit/redstone_active"), ConduitLang.REDSTONE_CONDUIT));
//
//        // Fluid conduits tick every 5 ticks, so remember the transfer rate per tick will be *5 for each operation.
//        context.register(Conduits.FLUID,
//            new FluidConduit(EnderIO.rl("block/conduit/fluid"), ConduitLang.FLUID_CONDUIT, 200, false, false));
//        context.register(Conduits.PRESSURIZED_FLUID, new FluidConduit(EnderIO.rl("block/conduit/pressurized_fluid"),
//            ConduitLang.PRESSURIZED_FLUID_CONDUIT, 1_000, false, true));
//        context.register(Conduits.ENDER_FLUID,
//            new FluidConduit(EnderIO.rl("block/conduit/ender_fluid"), ConduitLang.ENDER_FLUID_CONDUIT, 8_000, true, true));
//
//        context.register(Conduits.ITEM, new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ITEM_CONDUIT, 32, 20));
//        context.register(Conduits.ENHANCED_ITEM,
//            new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ENHANCED_ITEM_CONDUIT, 64, 20));
//        context.register(Conduits.ENDER_ITEM,
//            new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ENDER_ITEM_CONDUIT, 64, 10));
    }

    private static void register(BootstrapContext<Conduit<?, ?>> context, ResourceKey<Conduit<?, ?>> key, Function<Component, Conduit<?, ?>> factory) {
        context.register(key,
            factory.apply(Component.translatable(Util.makeDescriptionId(EnderIORegistries.Keys.CONDUIT.location().getPath(), key.location()))));
    }
}
