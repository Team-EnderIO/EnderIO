package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitDataSerializer;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitType;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitData;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitType;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemConduitTicker;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitType;
import com.enderio.conduits.common.conduit.type.SimpleConduitType;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EIOConduitTypes {

    public static class Types {
        public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPES, EnderIO.MODID);

        public static final DeferredHolder<ConduitType<?>, EnergyConduitType> ENERGY =
            CONDUIT_TYPES.register("energy", EnergyConduitType::new);

        public static final DeferredHolder<ConduitType<?>, RedstoneConduitType> REDSTONE =
            CONDUIT_TYPES.register("redstone", RedstoneConduitType::new);

        public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID =
            fluidConduit("fluid", 50, false);

        public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID2 =
            fluidConduit("pressurized_fluid", 100, false);

        public static final DeferredHolder<ConduitType<?>, FluidConduitType> FLUID3 =
            fluidConduit("ender_fluid", 200, true);

        public static final DeferredHolder<ConduitType<?>, SimpleConduitType<ItemConduitData>> ITEM =
            CONDUIT_TYPES.register("item",
                () -> new SimpleConduitType<>(
                    new ItemConduitTicker(),
                    ItemConduitData::new,
                    ConduitMenuData.ITEM));

        private static DeferredHolder<ConduitType<?>, FluidConduitType> fluidConduit(String name, int tier, boolean isMultiFluid) {
            return CONDUIT_TYPES.register(name,
                () -> new FluidConduitType(tier, isMultiFluid));
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
        Types.CONDUIT_TYPES.register(bus);
        Serializers.CONDUIT_DATA_SERIALIZERS.register(bus);
    }
}
