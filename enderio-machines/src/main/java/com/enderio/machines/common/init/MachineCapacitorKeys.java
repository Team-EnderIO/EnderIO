package com.enderio.machines.common.init;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.capacitor.CapacitorKeyType;
import com.enderio.api.capacitor.Scalers;
import com.enderio.base.EnderIO;
import com.enderio.machines.EIOMachines;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public enum MachineCapacitorKeys {
    SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY(() -> new CapacitorKey(2000, CapacitorKeyType.EnergyCapacity, Scalers.FIXED)),
    SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER(() -> new CapacitorKey(30, CapacitorKeyType.EnergyTransfer, Scalers.FIXED)),
    SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME(() -> new CapacitorKey(15, CapacitorKeyType.EnergyUsage, Scalers.FIXED)),

    ALLOY_SMELTER_ENERGY_CAPACITY(() -> new CapacitorKey(100000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY)),
    ALLOY_SMELTER_ENERGY_TRANSFER(() -> new CapacitorKey(120, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY)),
    ALLOY_SMELTER_ENERGY_CONSUME(() -> new CapacitorKey(30, CapacitorKeyType.EnergyUsage, Scalers.ENERGY)),

    ENHANCED_ALLOY_SMELTER_ENERGY_CAPACITY(() -> new CapacitorKey(1500000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY)),
    ENHANCED_ALLOY_SMELTER_ENERGY_TRANSFER(() -> new CapacitorKey(180, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY)),
    ENHANCED_ALLOY_SMELTER_ENERGY_CONSUME(() -> new CapacitorKey(45, CapacitorKeyType.EnergyUsage, Scalers.ENERGY)),

    SIMPLE_SAG_MILL_ENERGY_CAPACITY(() -> new CapacitorKey(2000, CapacitorKeyType.EnergyCapacity, Scalers.FIXED)),
    SIMPLE_SAG_MILL_ENERGY_TRANSFER(() -> new CapacitorKey(30, CapacitorKeyType.EnergyTransfer, Scalers.FIXED)),
    SIMPLE_SAG_MILL_ENERGY_CONSUME(() -> new CapacitorKey(15, CapacitorKeyType.EnergyUsage, Scalers.FIXED)),

    SAG_MILL_ENERGY_CAPACITY(() -> new CapacitorKey(100000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY)),
    SAG_MILL_ENERGY_TRANSFER(() -> new CapacitorKey(120, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY)),
    SAG_MILL_ENERGY_CONSUME(() -> new CapacitorKey(30, CapacitorKeyType.EnergyUsage, Scalers.ENERGY)),

    ENHANCED_SAG_MILL_ENERGY_CAPACITY(() -> new CapacitorKey(1500000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY)),
    ENHANCED_SAG_MILL_ENERGY_TRANSFER(() -> new CapacitorKey(180, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY)),
    ENHANCED_SAG_MILL_ENERGY_CONSUME(() -> new CapacitorKey(45, CapacitorKeyType.EnergyUsage, Scalers.ENERGY)),

    SLICE_AND_SPLICE_ENERGY_CAPACITY(() -> new CapacitorKey(100000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY)),
    SLICE_AND_SPLICE_ENERGY_TRANSFER(() -> new CapacitorKey(120, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY)),
    SLICE_AND_SPLICE_ENERGY_CONSUME(() -> new CapacitorKey(80, CapacitorKeyType.EnergyUsage, Scalers.ENERGY)),

    SIMPLE_STIRLING_GENERATOR_ENERGY_CAPACITY(() -> new CapacitorKey(2000, CapacitorKeyType.EnergyCapacity, Scalers.FIXED)),

    STIRLING_GENERATOR_ENERGY_CAPACITY(() -> new CapacitorKey(100000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY)),
    // TODO: Generation rate and efficiency.

    DEV_ENERGY_CAPACITY(() -> new CapacitorKey(100000, CapacitorKeyType.EnergyCapacity, Scalers.FIXED)),
    DEV_ENERGY_TRANSFER(() -> new CapacitorKey(120, CapacitorKeyType.EnergyTransfer, Scalers.FIXED)),
    DEV_ENERGY_CONSUME(() -> new CapacitorKey(30, CapacitorKeyType.EnergyUsage, Scalers.FIXED)),
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
