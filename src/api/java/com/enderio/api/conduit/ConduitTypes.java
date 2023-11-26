package com.enderio.api.conduit;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ConduitTypes {
    /**
     * @apiNote this DeferredRegister is not exposed for you, it's just a requirement to construct the ForgeRegistry.
     */
    public static final DeferredRegister<IConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(new ResourceLocation("enderio", "conduit_types"), "enderio");

    /**
     * Create a new DeferredRegister using this ForgeRegistry as a base
     */
    public static final Registry<IConduitType<?>> REGISTRY = CONDUIT_TYPES.makeRegistry(builder -> {});

    @SuppressWarnings("unchecked")
    public static Registry<IConduitType<?>> getRegistry() {
        //should always be a forgeRegistry. Needed for IDs for networking/ordering
        return REGISTRY;
    }

    public static void register(IEventBus bus) {
        CONDUIT_TYPES.register(bus);
    }
}
