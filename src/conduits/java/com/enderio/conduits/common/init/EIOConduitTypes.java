package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.ConduitNetworkType;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitNetworkContext;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitNetworkType;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitOptions;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitData;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitNetworkType;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitOptions;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemConduitNetworkType;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitNetworkType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOConduitTypes {

    public static class NetworkTypes {
        public static final DeferredRegister<ConduitNetworkType<?, ?, ?>> CONDUIT_GRAPH_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_NETWORK_TYPES, EnderIO.MODID);

        public static final Supplier<EnergyConduitNetworkType> ENERGY =
            CONDUIT_GRAPH_TYPES.register("energy", EnergyConduitNetworkType::new);

        public static final Supplier<RedstoneConduitNetworkType> REDSTONE =
            CONDUIT_GRAPH_TYPES.register("redstone", RedstoneConduitNetworkType::new);

        public static final Supplier<ItemConduitNetworkType> ITEM =
            CONDUIT_GRAPH_TYPES.register("item", ItemConduitNetworkType::new);

        public static final Supplier<FluidConduitNetworkType> FLUID =
            CONDUIT_GRAPH_TYPES.register("fluid", FluidConduitNetworkType::new);
    }

    public static class Types {
        public static final DeferredRegister<ConduitType<?, ?, ?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPES, EnderIO.MODID);

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<EnergyConduitOptions, EnergyConduitNetworkContext, ConduitData.EmptyConduitData>> ENERGY =
            register("energy", NetworkTypes.ENERGY, new EnergyConduitOptions(1000));

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<Void, ConduitNetworkContext.Dummy, RedstoneConduitData>> REDSTONE =
            register("redstone", NetworkTypes.REDSTONE, null);

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<FluidConduitOptions, ConduitNetworkContext.Dummy, FluidConduitData>> FLUID =
            register("fluid", NetworkTypes.FLUID, new FluidConduitOptions(false, 50));

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<FluidConduitOptions, ConduitNetworkContext.Dummy, FluidConduitData>> FLUID2 =
            register("fluid2", NetworkTypes.FLUID, new FluidConduitOptions(false, 100));

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<FluidConduitOptions, ConduitNetworkContext.Dummy, FluidConduitData>> FLUID3 =
            register("fluid3", NetworkTypes.FLUID, new FluidConduitOptions(true, 200));

        public static final DeferredHolder<ConduitType<?, ?, ?>, ConduitType<Void, ConduitNetworkContext.Dummy, ItemConduitData>> ITEM =
            register("item", NetworkTypes.ITEM, null);

        private static <T, U extends ConduitNetworkContext<U>, V extends ConduitData<V>, W extends ConduitNetworkType<T, U, V>> DeferredHolder<ConduitType<?, ?, ?>, ConduitType<T, U, V>> register(String name,
            Supplier<W> graphType, T options) {
            return CONDUIT_TYPES.register(name, () -> new ConduitType<>(graphType.get(), options));
        }
    }

    public static class Serializers {
        public static final DeferredRegister<ConduitDataSerializer<?>> CONDUIT_DATA_SERIALIZERS = DeferredRegister.create(EnderIORegistries.CONDUIT_DATA_SERIALIZERS, EnderIO.MODID);

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
        NetworkTypes.CONDUIT_GRAPH_TYPES.register(bus);
        Types.CONDUIT_TYPES.register(bus);
        Serializers.CONDUIT_DATA_SERIALIZERS.register(bus);
    }
}
