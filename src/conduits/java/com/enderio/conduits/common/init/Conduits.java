package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitNetworkContextSerializer;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitNetworkContext;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduit;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitData;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduit;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemConduit;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduit;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Conduits {

    public static ResourceKey<Conduit<?, ?, ?>> ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("energy"));
    public static ResourceKey<Conduit<?, ?, ?>> ENHANCED_ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("enhanced_energy"));
    public static ResourceKey<Conduit<?, ?, ?>> ENDER_ENERGY = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("ender_energy"));
    public static ResourceKey<Conduit<?, ?, ?>> REDSTONE = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("redstone"));
    public static ResourceKey<Conduit<?, ?, ?>> FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("fluid"));
    public static ResourceKey<Conduit<?, ?, ?>> PRESSURIZED_FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("pressurized_fluid"));
    public static ResourceKey<Conduit<?, ?, ?>> ENDER_FLUID = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("ender_fluid"));
    public static ResourceKey<Conduit<?, ?, ?>> ITEM = ResourceKey.create(EnderIORegistries.Keys.CONDUIT, EnderIO.loc("item"));

    public static void bootstrap(BootstrapContext<Conduit<?, ?, ?>> context) {
        // TODO: Is there a way to generate conditions for these? i.e. "neoforge:conditions":[{"type":"neoforge:mod_loaded","modid":"ae2"}]

        // TODO: Need to decide on transfer rates. These are just here to get the types in.
        context.register(ENERGY,
            new EnergyConduit(EnderIO.loc("block/conduit/energy"), ConduitLang.ENERGY_CONDUIT, 1000));
        context.register(ENHANCED_ENERGY,
            new EnergyConduit(EnderIO.loc("block/conduit/enhanced_energy"), ConduitLang.ENHANCED_ENERGY_CONDUIT, 5000));
        context.register(ENDER_ENERGY,
            new EnergyConduit(EnderIO.loc("block/conduit/ender_energy"), ConduitLang.ENDER_ENERGY_CONDUIT, 10000));

        context.register(REDSTONE, new RedstoneConduit(EnderIO.loc("block/conduit/redstone"), EnderIO.loc("block/conduit/redstone_active"),
            ConduitLang.REDSTONE_CONDUIT));

        context.register(FLUID,
            new FluidConduit(EnderIO.loc("block/conduit/fluid"), ConduitLang.FLUID_CONDUIT, 50, false));
        context.register(PRESSURIZED_FLUID,
            new FluidConduit(EnderIO.loc("block/conduit/pressurized_fluid"), ConduitLang.PRESSURIZED_FLUID_CONDUIT, 100, false));
        context.register(ENDER_FLUID,
            new FluidConduit(EnderIO.loc("block/conduit/ender_fluid"), ConduitLang.ENDER_FLUID_CONDUIT, 200, true));

        context.register(ITEM, new ItemConduit(EnderIO.loc("block/conduit/item"), ConduitLang.ITEM_CONDUIT));
    }

    public static class ContextSerializers {
        public static final DeferredRegister<ConduitNetworkContextSerializer<?>> CONDUIT_NETWORK_CONTEXT_SERIALIZERS =
            DeferredRegister.create(EnderIORegistries.CONDUIT_NETWORK_CONTEXT_SERIALIZER, EnderIO.MODID);

        public static final Supplier<EnergyConduitNetworkContext.Serializer> ENERGY =
            CONDUIT_NETWORK_CONTEXT_SERIALIZERS.register("energy", () -> EnergyConduitNetworkContext.Serializer.INSTANCE);
    }

    public static class Serializers {
        public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZER, EnderIO.MODID);

        // Register the API data type.
        public static DeferredHolder<ConduitDataSerializer<?>, ConduitData.EmptyConduitData.Serializer> EMPTY =
            CONDUIT_DATA_SERIALIZERS.register("empty", () -> ConduitData.EmptyConduitData.Serializer.INSTANCE);

        public static final Supplier<ConduitDataSerializer<FluidConduitData>> FLUID =
            CONDUIT_DATA_SERIALIZERS.register("fluid", FluidConduitData.Serializer::new);

        public static final Supplier<ConduitDataSerializer<RedstoneConduitData>> REDSTONE =
            CONDUIT_DATA_SERIALIZERS.register("redstone", RedstoneConduitData.Serializer::new);

        public static final Supplier<ConduitDataSerializer<ItemConduitData>> ITEM =
            CONDUIT_DATA_SERIALIZERS.register("item", ItemConduitData.Serializer::new);
    }

    public static void register(IEventBus bus) {
        ContextSerializers.CONDUIT_NETWORK_CONTEXT_SERIALIZERS.register(bus);
        Serializers.CONDUIT_DATA_SERIALIZERS.register(bus);
    }
}
