package com.enderio.enderio.datagen.common.datapack_registries;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduit;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduit;
import com.enderio.enderio.content.conduits.type.item.ItemConduit;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduit;
import com.enderio.enderio.init.ConduitLang;
import com.enderio.enderio.init.Conduits;
import net.minecraft.data.worldgen.BootstrapContext;

public class ConduitsBootstrap {
    public static void bootstrap(BootstrapContext<Conduit<?, ?>> context) {
        // TODO: These rates are still up for change, but will refine through testing.
        context.register(Conduits.ENERGY,
            new EnergyConduit(EnderIO.rl("block/conduit/energy"), ConduitLang.ENERGY_CONDUIT, 6000));
        context.register(Conduits.ENHANCED_ENERGY, new EnergyConduit(EnderIO.rl("block/conduit/enhanced_energy"),
            ConduitLang.ENHANCED_ENERGY_CONDUIT, 48_000));
        context.register(Conduits.ENDER_ENERGY,
            new EnergyConduit(EnderIO.rl("block/conduit/ender_energy"), ConduitLang.ENDER_ENERGY_CONDUIT, 384_000));

        context.register(Conduits.REDSTONE, new RedstoneConduit(EnderIO.rl("block/conduit/redstone"),
            EnderIO.rl("block/conduit/redstone_active"), ConduitLang.REDSTONE_CONDUIT));

        // Fluid conduits tick every 5 ticks, so remember the transfer rate per tick will be *5 for each operation.
        context.register(Conduits.FLUID,
            new FluidConduit(EnderIO.rl("block/conduit/fluid"), ConduitLang.FLUID_CONDUIT, 200, false, false));
        context.register(Conduits.PRESSURIZED_FLUID, new FluidConduit(EnderIO.rl("block/conduit/pressurized_fluid"),
            ConduitLang.PRESSURIZED_FLUID_CONDUIT, 1_000, false, true));
        context.register(Conduits.ENDER_FLUID,
            new FluidConduit(EnderIO.rl("block/conduit/ender_fluid"), ConduitLang.ENDER_FLUID_CONDUIT, 8_000, true, true));

        context.register(Conduits.ITEM, new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ITEM_CONDUIT, 32, 20));
        context.register(Conduits.ENHANCED_ITEM,
            new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ENHANCED_ITEM_CONDUIT, 64, 20));
        context.register(Conduits.ENDER_ITEM,
            new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ENDER_ITEM_CONDUIT, 64, 10));
    }
}
