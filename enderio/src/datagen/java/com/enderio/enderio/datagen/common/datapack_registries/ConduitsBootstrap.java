package com.enderio.enderio.datagen.common.datapack_registries;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitApi;
import com.enderio.enderio.content.conduits.type.energy.EnergyConduit;
import com.enderio.enderio.content.conduits.type.fluid.FluidConduit;
import com.enderio.enderio.content.conduits.type.item.ItemConduit;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduit;
import com.enderio.enderio.init.EIOConduits;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;

import java.util.function.Function;

public class ConduitsBootstrap {
    public static void bootstrap(BootstrapContext<Conduit<?, ?>> context) {
        // TODO: These rates are still up for change, but will refine through testing.

        register(context, EIOConduits.ENERGY, (desc) -> new EnergyConduit(EnderIO.id("block/conduit/energy"), desc, 6000));
        register(context, EIOConduits.ENHANCED_ENERGY, (desc) -> new EnergyConduit(EnderIO.id("block/conduit/enhanced_energy"), desc, 48_000));
        register(context, EIOConduits.ENDER_ENERGY, (desc) -> new EnergyConduit(EnderIO.id("block/conduit/ender_energy"), desc, 384_000));

        register(context, EIOConduits.REDSTONE,
            (desc) -> new RedstoneConduit(EnderIO.id("block/conduit/redstone"), EnderIO.id("block/conduit/redstone_active"), desc));

        // Fluid conduits tick every 5 ticks, so remember the transfer rate per tick will be *5 for each operation.
        register(context, EIOConduits.FLUID, (desc) -> new FluidConduit(EnderIO.id("block/conduit/fluid"), desc, 200, false, false));
        register(context, EIOConduits.PRESSURIZED_FLUID, (desc) -> new FluidConduit(EnderIO.id("block/conduit/pressurized_fluid"), desc, 1_000, false, true));
        register(context, EIOConduits.ENDER_FLUID, (desc) -> new FluidConduit(EnderIO.id("block/conduit/ender_fluid"), desc, 8_000, true, true));

        register(context, EIOConduits.ITEM, (desc) -> new ItemConduit(EnderIO.id("block/conduit/item"), desc, 32, 20));
        register(context, EIOConduits.ENHANCED_ITEM, (desc) -> new ItemConduit(EnderIO.id("block/conduit/item"), desc, 64, 20));
        register(context, EIOConduits.ENDER_ITEM, (desc) -> new ItemConduit(EnderIO.id("block/conduit/item"), desc, 64, 10));
    }

    private static void register(BootstrapContext<Conduit<?, ?>> context, ResourceKey<Conduit<?, ?>> key, Function<Component, Conduit<?, ?>> factory) {
        context.register(key,
            factory.apply(Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(key))));
    }
}
