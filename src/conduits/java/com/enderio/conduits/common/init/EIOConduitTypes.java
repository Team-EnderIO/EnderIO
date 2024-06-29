package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitGraphContext;
import com.enderio.api.conduit.ConduitGraphType;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitData;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitGraphType;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitData;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitGraphType;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitOptions;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemConduitGraphType;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitGraphType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOConduitTypes {

    public static class Graphs {
        public static final DeferredRegister<ConduitGraphType<?, ?, ?>> CONDUIT_GRAPH_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_GRAPH_TYPES, EnderIO.MODID);

        public static final Supplier<EnergyConduitGraphType> ENERGY =
            CONDUIT_GRAPH_TYPES.register("energy", EnergyConduitGraphType::new);

        public static final Supplier<RedstoneConduitGraphType> REDSTONE =
            CONDUIT_GRAPH_TYPES.register("redstone", RedstoneConduitGraphType::new);

        public static final Supplier<ItemConduitGraphType> ITEM =
            CONDUIT_GRAPH_TYPES.register("item", ItemConduitGraphType::new);

        public static final Supplier<FluidConduitGraphType> FLUID =
            CONDUIT_GRAPH_TYPES.register("fluid", FluidConduitGraphType::new);
    }

    public static class Types {
        public static final DeferredRegister<ConduitType<?, ?, ?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPES, EnderIO.MODID);

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<Void, ConduitGraphContext.Dummy, EnergyConduitData>> ENERGY =
            register("energy", Graphs.ENERGY, null);

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<Void, ConduitGraphContext.Dummy, RedstoneConduitData>> REDSTONE =
            register("redstone", Graphs.REDSTONE, null);

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<FluidConduitOptions, ConduitGraphContext.Dummy, FluidConduitData>> FLUID =
            register("fluid", Graphs.FLUID, new FluidConduitOptions(false, 50));

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<FluidConduitOptions, ConduitGraphContext.Dummy, FluidConduitData>> FLUID2 =
            register("fluid2", Graphs.FLUID, new FluidConduitOptions(false, 100));

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<FluidConduitOptions, ConduitGraphContext.Dummy, FluidConduitData>> FLUID3 =
            register("fluid3", Graphs.FLUID, new FluidConduitOptions(true, 200));

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<Void, ConduitGraphContext.Dummy, ItemConduitData>> ITEM =
            register("item", Graphs.ITEM, null);

        private static <T, U extends ConduitGraphContext<U>, V extends ConduitData<V>, W extends ConduitGraphType<T, U, V>> DeferredHolder<ConduitType<?, ?, ?>, ConduitType<T, U, V>> register(String name,
            Supplier<W> graphType, T options) {
            return CONDUIT_TYPES.register(name, () -> new ConduitType<>(graphType.get(), options));
        }
    }

    public static class Serializers {
        public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZERS, EnderIO.MODID);

        // Register the API data type.
        public static DeferredHolder<ConduitDataSerializer<?>, ConduitData.EmptyConduitData.Serializer> EMPTY =
            CONDUIT_DATA_SERIALIZERS.register("empty", () -> ConduitData.EmptyConduitData.Serializer.INSTANCE);

        public static final Supplier<ConduitDataSerializer<EnergyConduitData>> ENERGY =
            CONDUIT_DATA_SERIALIZERS.register("energy", EnergyConduitData.Serializer::new);

        public static final Supplier<ConduitDataSerializer<FluidConduitData>> FLUID =
            CONDUIT_DATA_SERIALIZERS.register("fluid", FluidConduitData.Serializer::new);

        public static final Supplier<ConduitDataSerializer<RedstoneConduitData>> REDSTONE =
            CONDUIT_DATA_SERIALIZERS.register("redstone", RedstoneConduitData.Serializer::new);

        public static final Supplier<ConduitDataSerializer<ItemConduitData>> ITEM =
            CONDUIT_DATA_SERIALIZERS.register("item", ItemConduitData.Serializer::new);
    }

    public static void register(IEventBus bus) {
        Graphs.CONDUIT_GRAPH_TYPES.register(bus);
        Types.CONDUIT_TYPES.register(bus);
        Serializers.CONDUIT_DATA_SERIALIZERS.register(bus);
    }
}
