package com.enderio.conduits.common.init;

import com.enderio.EnderIOBase;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.ConduitDataType;
import com.enderio.conduits.api.ConduitType;
import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduit;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduit;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitData;
import com.enderio.conduits.common.conduit.type.item.ItemConduit;
import com.enderio.conduits.common.conduit.type.item.ItemConduitData;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduit;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ConduitTypes {
    private static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_TYPE, EnderIOConduits.REGISTRY_NAMESPACE);

    public static final Supplier<ConduitType<EnergyConduit>> ENERGY = CONDUIT_TYPES
        .register("energy", () -> ConduitType.builder(EnergyConduit.CODEC)
            .exposeCapability(Capabilities.EnergyStorage.BLOCK)
            .build());

    public static final Supplier<ConduitType<RedstoneConduit>> REDSTONE = CONDUIT_TYPES
        .register("redstone", () -> ConduitType.of(RedstoneConduit.CODEC));

    public static final Supplier<ConduitType<FluidConduit>> FLUID = CONDUIT_TYPES
        .register("fluid", () -> ConduitType.of(FluidConduit.CODEC));

    public static final Supplier<ConduitType<ItemConduit>> ITEM = CONDUIT_TYPES
        .register("item", () -> ConduitType.of(ItemConduit::new));

    public static class Data {
        private static final DeferredRegister<ConduitDataType<?>> CONDUIT_DATA_TYPES = DeferredRegister.create(EnderIOConduitsRegistries.CONDUIT_DATA_TYPE, EnderIOBase.REGISTRY_NAMESPACE);

        public static final Supplier<ConduitDataType<ItemConduitData>> ITEM = CONDUIT_DATA_TYPES
            .register("item", () -> new ConduitDataType<>(ItemConduitData.CODEC, ItemConduitData.STREAM_CODEC, ItemConduitData::new));

        public static final Supplier<ConduitDataType<FluidConduitData>> FLUID = CONDUIT_DATA_TYPES
            .register("fluid", () -> new ConduitDataType<>(FluidConduitData.CODEC, FluidConduitData.STREAM_CODEC, FluidConduitData::new));

        public static final Supplier<ConduitDataType<RedstoneConduitData>> REDSTONE = CONDUIT_DATA_TYPES
            .register("redstone", () -> new ConduitDataType<>(RedstoneConduitData.CODEC, RedstoneConduitData.STREAM_CODEC, RedstoneConduitData::new));
    }

    public static void register(IEventBus bus) {
        CONDUIT_TYPES.register(bus);
        Data.CONDUIT_DATA_TYPES.register(bus);
    }
}
