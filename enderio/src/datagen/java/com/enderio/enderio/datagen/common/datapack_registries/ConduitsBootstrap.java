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
import net.minecraft.Util;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;

public class ConduitsBootstrap {
    public static void bootstrap(BootstrapContext<Conduit<?, ?>> context) {
        // TODO: These rates are still up for change, but will refine through testing.

        // TODO(1.21.1+): change resource keys to match the names
        register(context, EIOConduits.ENERGY, (desc) -> new EnergyConduit(EnderIO.rl("block/conduit/energy"), desc, Optional.empty(), 8000));
        register(context, EIOConduits.ENERGETIC_ENERGY, (desc) -> new EnergyConduit(EnderIO.rl("block/conduit/enhanced_energy"), desc, Optional.empty(), 48_000));
        register(context, EIOConduits.VIBRANT_ENERGY, (desc) -> new EnergyConduit(EnderIO.rl("block/conduit/ender_energy"), desc, Optional.empty(), 192_000));

        register(context, EIOConduits.REDSTONE,
            (desc) -> new RedstoneConduit(EnderIO.rl("block/conduit/redstone"), EnderIO.rl("block/conduit/redstone_active"), desc, Optional.empty()));

        // Fluid conduits tick every 5 ticks, so remember the transfer rate per tick will be *5 for each operation.
        register(context, EIOConduits.FLUID, (desc) -> new FluidConduit(EnderIO.rl("block/conduit/fluid"), desc, Optional.empty(), 200));
        register(context, EIOConduits.ENERGETIC_FLUID, (desc) -> new FluidConduit(EnderIO.rl("block/conduit/pressurized_fluid"), desc, Optional.empty(), 1_000));
        register(context, EIOConduits.VIBRANT_FLUID, (desc) -> new FluidConduit(EnderIO.rl("block/conduit/ender_fluid"), desc, Optional.empty(), 8_000));

        register(context, EIOConduits.ITEM, (desc) -> new ItemConduit(EnderIO.rl("block/conduit/item"), desc, Optional.empty(), 32, 20));
        register(context, EIOConduits.ENERGETIC_ITEM, (desc) -> new ItemConduit(EnderIO.rl("block/conduit/item"), desc, Optional.empty(), 64, 20));
        register(context, EIOConduits.VIBRANT_ITEM, (desc) -> new ItemConduit(EnderIO.rl("block/conduit/item"), desc, Optional.empty(), 64, 10));
    }

    private static void register(BootstrapContext<Conduit<?, ?>> context, ResourceKey<Conduit<?, ?>> key, Function<Component, Conduit<?, ?>> factory) {
        context.register(key,
            factory.apply(Component.translatable(ConduitApi.INSTANCE.makeDescriptionId(key))));
    }
}
