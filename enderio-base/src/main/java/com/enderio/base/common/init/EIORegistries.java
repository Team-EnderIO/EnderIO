package com.enderio.base.common.init;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.EnderIO;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class EIORegistries {
    // Registry keys
    public static final ResourceKey<Registry<CapacitorKey>> CAPACITOR_KEYS = key(EnderIO.loc("capacitor_keys"));

    // Registry creation
    private static DeferredRegister<CapacitorKey> DEFERRED_CAPACITOR_KEYS = DeferredRegister.create(CAPACITOR_KEYS, EnderIO.MODID);
    public static final Supplier<IForgeRegistry<CapacitorKey>> CAPACITOR_KEYS_REGISTRY = DEFERRED_CAPACITOR_KEYS.makeRegistry(EIORegistries::capacitorKeyRegistryBuilder);

    private static <T> ResourceKey<Registry<T>> key(ResourceLocation id) {
        return ResourceKey.createRegistryKey(id);
    }

    private static RegistryBuilder<CapacitorKey> capacitorKeyRegistryBuilder() {
        return new RegistryBuilder<CapacitorKey>().setName(CAPACITOR_KEYS.location());
    }

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DEFERRED_CAPACITOR_KEYS.register(bus);
    }
}
