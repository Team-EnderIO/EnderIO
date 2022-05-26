package com.enderio.machines.common.init;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.capacitor.Scalers;
import com.enderio.base.EnderIO;
import com.enderio.machines.EIOMachines;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public enum MachineCapacitorKeys {
    SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY(() -> new CapacitorKey(2000, Scalers.FIXED)),
    SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER(() -> new CapacitorKey(15, Scalers.FIXED)),
    SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME(() -> new CapacitorKey(30, Scalers.FIXED)),

    ALLOY_SMELTER_ENERGY_CAPACITY(() -> new CapacitorKey(100000, Scalers.ENERGY)),
    ALLOY_SMELTER_ENERGY_TRANSFER(() -> new CapacitorKey(120, Scalers.ENERGY)),
    ALLOY_SMELTER_ENERGY_CONSUME(() -> new CapacitorKey(30, Scalers.ENERGY)),

    ENHANCED_ALLOY_SMELTER_ENERGY_CAPACITY(() -> new CapacitorKey(1500000, Scalers.ENERGY)),
    ENHANCED_ALLOY_SMELTER_ENERGY_TRANSFER(() -> new CapacitorKey(180, Scalers.ENERGY)),
    ENHANCED_ALLOY_SMELTER_ENERGY_CONSUME(() -> new CapacitorKey(45, Scalers.ENERGY)),

    SIMPLE_STIRLING_GENERATOR_ENERGY_CAPACITY(() -> new CapacitorKey(2000, Scalers.FIXED)),

    STIRLING_GENERATOR_ENERGY_CAPACITY(() -> new CapacitorKey(100000, Scalers.ENERGY)),
    // TODO: Generation rate and efficiency.

    DEV_ENERGY_CAPACITY(() -> new CapacitorKey(100000, Scalers.FIXED)),
    DEV_ENERGY_TRANSFER(() -> new CapacitorKey(120, Scalers.FIXED)),
    DEV_ENERGY_CONSUME(() -> new CapacitorKey(30, Scalers.FIXED)),
    ;

    // In a subclass so that its loaded.
    private static class Register {
        private static final DeferredRegister<CapacitorKey> CAPACITOR_KEYS = DeferredRegister.create(EnderIO.CAPACITOR_KEY_REGISTRY_KEY, EIOMachines.MODID);
    }

    public static void classload() {
        Register.CAPACITOR_KEYS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private final RegistryObject<CapacitorKey> registryObject;

    MachineCapacitorKeys(Supplier<CapacitorKey> factory) {
        registryObject = Register.CAPACITOR_KEYS.register(name().toLowerCase(), factory);
    }

    MachineCapacitorKeys(String name, Supplier<CapacitorKey> factory) {
        registryObject = Register.CAPACITOR_KEYS.register(name, factory);
    }

    public RegistryObject<CapacitorKey> getRegistryObject() {
        return registryObject;
    }

    public CapacitorKey get() {
        return registryObject.get();
    }
}
