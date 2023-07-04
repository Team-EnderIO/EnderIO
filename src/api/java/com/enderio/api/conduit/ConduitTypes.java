package com.enderio.api.conduit;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ConduitTypes {
    /**
     * @apiNote this DeferredRegister is not exposed for you, it's just a requirement to construct the ForgeRegistry.
     */
    public static final DeferredRegister<IConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(new ResourceLocation("enderio", "conduit_types"), "enderio");

    /**
     * Create a new DeferredRegister using this ForgeRegistry as a base
     */
    public static final Supplier<IForgeRegistry<IConduitType<?>>> REGISTRY = CONDUIT_TYPES.makeRegistry(RegistryBuilder::new);

    public static ForgeRegistry<IConduitType<?>> getRegistry() {
        //should always be a forgeRegistry. Needed for IDs for networking/ordering
        return (ForgeRegistry<IConduitType<?>>) REGISTRY.get();
    }

    public static void register(IEventBus bus) {
        CONDUIT_TYPES.register(bus);
    }
}
