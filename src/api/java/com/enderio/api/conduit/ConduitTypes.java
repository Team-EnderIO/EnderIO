package com.enderio.api.conduit;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ConduitTypes {
    /**
     * @apiNote this DeferredRegister is not exposed for you, it's just a requirement to construct the ForgeRegistry.
     * Use {@link ConduitTypes#REGISTRY} instead
     */
    private static final DeferredRegister<IConduitType> CONDUIT_TYPES = DeferredRegister.create(new ResourceLocation("enderio", "conduit_types"), "enderio");

    /**
     * Create a new DeferredRegister using this ForgeRegistry as a base
     */
    public static final Supplier<IForgeRegistry<IConduitType>> REGISTRY = CONDUIT_TYPES.makeRegistry(RegistryBuilder::new);
}
