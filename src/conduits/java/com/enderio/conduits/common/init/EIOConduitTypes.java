package com.enderio.conduits.common.init;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.type.energy.EnergyConduitType;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitType;
import com.enderio.conduits.common.conduit.type.item.ItemConduitType;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class EIOConduitTypes {
    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(EnderIORegistries.Keys.CONDUIT_TYPES, EnderIO.MODID);

    public static final Supplier<IForgeRegistry<ConduitType<?>>> REGISTRY = CONDUIT_TYPES.makeRegistry(RegistryBuilder::new);

    // TODO: 1.20.1: Remove these and just use the ResourceLocations instead.
    public static int getConduitId(ConduitType<?> type) {
        //noinspection UnstableApiUsage
        return ((ForgeRegistry<ConduitType<?>>)REGISTRY.get()).getID(type);
    }

    public static ConduitType<?> getById(int id) {
        //noinspection UnstableApiUsage
        return ((ForgeRegistry<ConduitType<?>>)REGISTRY.get()).getValue(id);
    }

    public static final RegistryObject<EnergyConduitType> ENERGY =
        CONDUIT_TYPES.register("energy_conduit", EnergyConduitType::new);

    public static final RegistryObject<RedstoneConduitType> REDSTONE =
        CONDUIT_TYPES.register("redstone_conduit", RedstoneConduitType::new);

    public static final RegistryObject<FluidConduitType> FLUID =
        fluidConduit("fluid_conduit", 100, false);

    public static final RegistryObject<FluidConduitType> FLUID2 =
        fluidConduit("pressurized_fluid_conduit", 1_000, false);

    public static final RegistryObject<FluidConduitType> FLUID3 =
        fluidConduit("ender_fluid_conduit", 10_000, true);

    public static final RegistryObject<ItemConduitType> ITEM =
        CONDUIT_TYPES.register("item_conduit", ItemConduitType::new);

    private static RegistryObject<FluidConduitType> fluidConduit(String name, int tier, boolean isMultiFluid) {
        return CONDUIT_TYPES.register(name,
            () -> new FluidConduitType(EnderIO.loc(name), tier, isMultiFluid));
    }

    public static void register(IEventBus bus) {
        CONDUIT_TYPES.register(bus);
    }
}
