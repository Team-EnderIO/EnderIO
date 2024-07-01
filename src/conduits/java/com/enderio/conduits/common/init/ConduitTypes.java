package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduit;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduit;
import com.enderio.conduits.common.conduit.type.item.ItemConduit;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduit;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ConduitTypes {
    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.CONDUIT_TYPE, EnderIO.MODID);

    public static final Supplier<ConduitType<EnergyConduit>> ENERGY = CONDUIT_TYPES
        .register("energy", () -> ConduitType.builder(EnergyConduit.CODEC)
            .exposeCapability(Capabilities.EnergyStorage.BLOCK)
            .build());

    public static final Supplier<ConduitType<RedstoneConduit>> REDSTONE = CONDUIT_TYPES
        .register("redstone", () -> ConduitType.of(RedstoneConduit::new));

    public static final Supplier<ConduitType<FluidConduit>> FLUID = CONDUIT_TYPES
        .register("fluid", () -> ConduitType.of(FluidConduit.CODEC));

    public static final Supplier<ConduitType<ItemConduit>> ITEM = CONDUIT_TYPES
        .register("item", () -> ConduitType.of(ItemConduit::new));

    public static void register(IEventBus bus) {
        CONDUIT_TYPES.register(bus);
    }
}
