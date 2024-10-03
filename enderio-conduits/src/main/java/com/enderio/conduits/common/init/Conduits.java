package com.enderio.conduits.common.init;

import com.enderio.EnderIOBase;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.ConduitNetworkContextType;
import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduit;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitNetworkContext;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduit;
import com.enderio.conduits.common.conduit.type.item.ItemConduit;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduit;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Conduits {

    public static ResourceKey<Conduit<?>> ENERGY = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("energy"));
    public static ResourceKey<Conduit<?>> ENHANCED_ENERGY = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("enhanced_energy"));
    public static ResourceKey<Conduit<?>> ENDER_ENERGY = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("ender_energy"));
    public static ResourceKey<Conduit<?>> REDSTONE = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("redstone"));
    public static ResourceKey<Conduit<?>> FLUID = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("fluid"));
    public static ResourceKey<Conduit<?>> PRESSURIZED_FLUID = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("pressurized_fluid"));
    public static ResourceKey<Conduit<?>> ENDER_FLUID = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("ender_fluid"));
    public static ResourceKey<Conduit<?>> ITEM = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("item"));
    public static ResourceKey<Conduit<?>> ENHANCED_ITEM = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("enhanced_item"));
    public static ResourceKey<Conduit<?>> ENDER_ITEM = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT, EnderIOBase.loc("ender_item"));

    public static void bootstrap(BootstrapContext<Conduit<?>> context) {
        // TODO: These rates are still up for change, but will refine through testing.
        context.register(ENERGY,
            new EnergyConduit(EnderIOBase.loc("block/conduit/energy"), ConduitLang.ENERGY_CONDUIT, 1_000));
        context.register(ENHANCED_ENERGY,
            new EnergyConduit(EnderIOBase.loc("block/conduit/enhanced_energy"), ConduitLang.ENHANCED_ENERGY_CONDUIT, 12_000));
        context.register(ENDER_ENERGY,
            new EnergyConduit(EnderIOBase.loc("block/conduit/ender_energy"), ConduitLang.ENDER_ENERGY_CONDUIT, 48_000));

        context.register(REDSTONE, new RedstoneConduit(EnderIOBase.loc("block/conduit/redstone"), EnderIOBase.loc("block/conduit/redstone_active"),
            ConduitLang.REDSTONE_CONDUIT));

        context.register(FLUID,
            new FluidConduit(EnderIOBase.loc("block/conduit/fluid"), ConduitLang.FLUID_CONDUIT, 500, false));
        context.register(PRESSURIZED_FLUID,
            new FluidConduit(EnderIOBase.loc("block/conduit/pressurized_fluid"), ConduitLang.PRESSURIZED_FLUID_CONDUIT, 2_500, false));
        context.register(ENDER_FLUID,
            new FluidConduit(EnderIOBase.loc("block/conduit/ender_fluid"), ConduitLang.ENDER_FLUID_CONDUIT, 10_000, true));

        context.register(ITEM, new ItemConduit(EnderIOBase.loc("block/conduit/item"), ConduitLang.ITEM_CONDUIT, 4, 20));

        // TODO: Implement the new item conduit tiers.
        //context.register(ENHANCED_ITEM, new ItemConduit(EnderIOBase.loc("block/conduit/item"), ConduitLang.ENHANCED_ITEM_CONDUIT, 4, 10));
        //context.register(ENDER_ITEM, new ItemConduit(EnderIOBase.loc("block/conduit/item"), ConduitLang.ENDER_ITEM_CONDUIT, 4, 5));
    }

    public static class ContextSerializers {
        public static final DeferredRegister<ConduitNetworkContextType<?>> CONDUIT_NETWORK_CONTEXT_TYPES =
            DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_NETWORK_CONTEXT_TYPE, EnderIOConduits.REGISTRY_NAMESPACE);

        public static final Supplier<ConduitNetworkContextType<EnergyConduitNetworkContext>> ENERGY =
            CONDUIT_NETWORK_CONTEXT_TYPES.register("energy", () -> new ConduitNetworkContextType<>(EnergyConduitNetworkContext.CODEC,
                EnergyConduitNetworkContext::new));
    }

    public static void register(IEventBus bus) {
        ContextSerializers.CONDUIT_NETWORK_CONTEXT_TYPES.register(bus);
    }
}
