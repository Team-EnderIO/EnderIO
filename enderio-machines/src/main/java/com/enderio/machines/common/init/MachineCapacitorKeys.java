package com.enderio.machines.common.init;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.capacitor.CapacitorKeyType;
import com.enderio.api.capacitor.IScaler;
import com.enderio.api.capacitor.Scalers;
import com.enderio.base.common.init.EIORegistries;
import com.enderio.machines.EIOMachines;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public enum MachineCapacitorKeys {
    SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY(2000, CapacitorKeyType.EnergyCapacity, Scalers.FIXED),
    SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER(30, CapacitorKeyType.EnergyTransfer, Scalers.FIXED),
    SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME(15, CapacitorKeyType.EnergyUsage, Scalers.FIXED),

    ALLOY_SMELTER_ENERGY_CAPACITY(100000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY),
    ALLOY_SMELTER_ENERGY_TRANSFER(120, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY),
    ALLOY_SMELTER_ENERGY_CONSUME(30, CapacitorKeyType.EnergyUsage, Scalers.ENERGY),

    ENHANCED_ALLOY_SMELTER_ENERGY_CAPACITY(1500000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY),
    ENHANCED_ALLOY_SMELTER_ENERGY_TRANSFER(180, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY),
    ENHANCED_ALLOY_SMELTER_ENERGY_CONSUME(45, CapacitorKeyType.EnergyUsage, Scalers.ENERGY),

    SIMPLE_SAG_MILL_ENERGY_CAPACITY(2000, CapacitorKeyType.EnergyCapacity, Scalers.FIXED),
    SIMPLE_SAG_MILL_ENERGY_TRANSFER(30, CapacitorKeyType.EnergyTransfer, Scalers.FIXED),
    SIMPLE_SAG_MILL_ENERGY_CONSUME(15, CapacitorKeyType.EnergyUsage, Scalers.FIXED),

    SAG_MILL_ENERGY_CAPACITY(100000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY),
    SAG_MILL_ENERGY_TRANSFER(120, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY),
    SAG_MILL_ENERGY_CONSUME(30, CapacitorKeyType.EnergyUsage, Scalers.ENERGY),

    ENHANCED_SAG_MILL_ENERGY_CAPACITY(1500000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY),
    ENHANCED_SAG_MILL_ENERGY_TRANSFER(180, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY),
    ENHANCED_SAG_MILL_ENERGY_CONSUME(45, CapacitorKeyType.EnergyUsage, Scalers.ENERGY),

    SLICE_AND_SPLICE_ENERGY_CAPACITY(100000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY),
    SLICE_AND_SPLICE_ENERGY_TRANSFER(120, CapacitorKeyType.EnergyTransfer, Scalers.ENERGY),
    SLICE_AND_SPLICE_ENERGY_CONSUME(80, CapacitorKeyType.EnergyUsage, Scalers.ENERGY),

    SIMPLE_STIRLING_GENERATOR_ENERGY_CAPACITY(2000, CapacitorKeyType.EnergyCapacity, Scalers.FIXED),

    STIRLING_GENERATOR_ENERGY_CAPACITY(100000, CapacitorKeyType.EnergyCapacity, Scalers.ENERGY),
    // TODO: Generation rate and efficiency.

    DEV_ENERGY_CAPACITY(100000, CapacitorKeyType.EnergyCapacity, Scalers.FIXED),
    DEV_ENERGY_TRANSFER(120, CapacitorKeyType.EnergyTransfer, Scalers.FIXED),
    DEV_ENERGY_CONSUME(30, CapacitorKeyType.EnergyUsage, Scalers.FIXED),
    ;

    // In a subclass so that its loaded.
    private static class Register {
        private static final DeferredRegister<CapacitorKey> CAPACITOR_KEYS = DeferredRegister.create(EIORegistries.CAPACITOR_KEYS, EIOMachines.MODID);
    }

    public static void register() {
        Register.CAPACITOR_KEYS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private final RegistryObject<CapacitorKey> registryObject;

    MachineCapacitorKeys(float base, CapacitorKeyType type, IScaler scaler) {
        registryObject = Register.CAPACITOR_KEYS.register(name().toLowerCase(), () -> new CapacitorKey(base, type, scaler));
    }

    MachineCapacitorKeys(String name, float base, CapacitorKeyType type, IScaler scaler) {
        registryObject = Register.CAPACITOR_KEYS.register(name, () -> new CapacitorKey(base, type, scaler));
    }

    public RegistryObject<CapacitorKey> getRegistryObject() {
        return registryObject;
    }

    // TODO: is it a good idea to just return the RegistryObject only?
    public CapacitorKey get() {
        return registryObject.get();
    }
}
