package com.enderio.enderio.datagen.common.datapack_registries;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduit;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduit;
import com.enderio.enderio.content.conduits.type.item.ItemConduit;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduit;
import com.enderio.enderio.init.ConduitLang;
import com.enderio.enderio.init.EIOConduits;
import net.minecraft.data.worldgen.BootstrapContext;

public class ConduitsBootstrap {
    public static void bootstrap(BootstrapContext<Conduit<?, ?>> context) {
        // TODO: These rates are still up for change, but will refine through testing.
        context.register(EIOConduits.ENERGY,
            new EnergyConduit(EnderIO.rl("block/conduit/energy"), ConduitLang.ENERGY_CONDUIT, 6000));
        context.register(EIOConduits.ENHANCED_ENERGY, new EnergyConduit(EnderIO.rl("block/conduit/enhanced_energy"),
            ConduitLang.ENHANCED_ENERGY_CONDUIT, 48_000));
        context.register(EIOConduits.ENDER_ENERGY,
            new EnergyConduit(EnderIO.rl("block/conduit/ender_energy"), ConduitLang.ENDER_ENERGY_CONDUIT, 384_000));

        context.register(EIOConduits.REDSTONE, new RedstoneConduit(EnderIO.rl("block/conduit/redstone"),
            EnderIO.rl("block/conduit/redstone_active"), ConduitLang.REDSTONE_CONDUIT));

        // Fluid conduits tick every 5 ticks, so remember the transfer rate per tick will be *5 for each operation.
        context.register(EIOConduits.FLUID,
            new FluidConduit(EnderIO.rl("block/conduit/fluid"), ConduitLang.FLUID_CONDUIT, 200, false, false));
        context.register(EIOConduits.PRESSURIZED_FLUID, new FluidConduit(EnderIO.rl("block/conduit/pressurized_fluid"),
            ConduitLang.PRESSURIZED_FLUID_CONDUIT, 1_000, false, true));
        context.register(EIOConduits.ENDER_FLUID,
            new FluidConduit(EnderIO.rl("block/conduit/ender_fluid"), ConduitLang.ENDER_FLUID_CONDUIT, 8_000, true, true));

        context.register(EIOConduits.ITEM, new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ITEM_CONDUIT, 32, 20));
        context.register(EIOConduits.ENHANCED_ITEM,
            new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ENHANCED_ITEM_CONDUIT, 64, 20));
        context.register(EIOConduits.ENDER_ITEM,
            new ItemConduit(EnderIO.rl("block/conduit/item"), ConduitLang.ENDER_ITEM_CONDUIT, 64, 10));
    }
}
