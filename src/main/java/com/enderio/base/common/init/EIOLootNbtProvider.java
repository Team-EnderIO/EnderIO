package com.enderio.base.common.init;

import com.enderio.EnderIO;
import com.enderio.base.common.loot.providers.nbt.ImprovedContextNbtProvider.JsonSerializer;

import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EIOLootNbtProvider {
    private static final DeferredRegister<LootNbtProviderType> LOOT_NBT_PROVIDER_REGISTRY = DeferredRegister
            .create(Registry.LOOT_NBT_PROVIDER_REGISTRY, EnderIO.MODID);
    public static final RegistryObject<LootNbtProviderType> IMPROVED_CONTEXT = LOOT_NBT_PROVIDER_REGISTRY
            .register("improved_context", () -> new LootNbtProviderType(new JsonSerializer()));

    public static void register() {
        IEventBus eventbus = FMLJavaModLoadingContext.get().getModEventBus();
        LOOT_NBT_PROVIDER_REGISTRY.register(eventbus);
    }
}
