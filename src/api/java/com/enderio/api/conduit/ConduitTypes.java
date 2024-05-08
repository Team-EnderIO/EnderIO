package com.enderio.api.conduit;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConduitTypes {
    /**
     * @apiNote this DeferredRegister is not exposed for you, it's just a requirement to construct the ForgeRegistry.
     */
    public static final DeferredRegister<ConduitType<?>> CONDUIT_TYPES = DeferredRegister.create(new ResourceLocation("enderio", "conduit_types"), "enderio");

    /**
     * Create a new DeferredRegister using this Registry as a base
     */
    public static final Registry<ConduitType<?>> REGISTRY = CONDUIT_TYPES.makeRegistry(builder -> {});

    /**
     * @deprecated Use {@link ConduitTypes#REGISTRY} instead.
     */
    @Deprecated(forRemoval = true, since = "6.1")
    public static Registry<ConduitType<?>> getRegistry() {
        //should always be a forgeRegistry. Needed for IDs for networking/ordering
        return REGISTRY;
    }

    public static void register(IEventBus bus) {
        CONDUIT_TYPES.register(bus);
    }
}
