package com.enderio.machines.common.init;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.capacitor.Scalers;
import com.enderio.base.EnderIO;
import com.enderio.machines.EIOMachines;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MachineCapacitorKeys {
    // TODO: Might want to turn this into an enum?

    private static final DeferredRegister<CapacitorKey> CAPACITOR_KEYS = DeferredRegister.create(EnderIO.CAPACITOR_KEY_REGISTRY_KEY, EIOMachines.MODID);

    // TODO: If this is used, one day configs should be explored.

    public static final RegistryObject<CapacitorKey> SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY = CAPACITOR_KEYS.register("simple_alloy_smelter_capacity", () -> new CapacitorKey(2000, Scalers.FIXED));
    public static final RegistryObject<CapacitorKey> SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER = CAPACITOR_KEYS.register("simple_alloy_smelter_transfer", () -> new CapacitorKey(15, Scalers.FIXED));
    public static final RegistryObject<CapacitorKey> SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME = CAPACITOR_KEYS.register("simple_alloy_smelter_consume", () -> new CapacitorKey(30, Scalers.FIXED));

    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_CAPACITY = CAPACITOR_KEYS.register("alloy_smelter_capacity", () -> new CapacitorKey(100000, Scalers.ENERGY));
    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_TRANSFER = CAPACITOR_KEYS.register("alloy_smelter_transfer", () -> new CapacitorKey(120, Scalers.ENERGY));
    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_CONSUME = CAPACITOR_KEYS.register("alloy_smelter_consume", () -> new CapacitorKey(30, Scalers.ENERGY));

    public static final RegistryObject<CapacitorKey> ENHANCED_ALLOY_SMELTER_ENERGY_CAPACITY = CAPACITOR_KEYS.register("enhanced_alloy_smelter_capacity", () -> new CapacitorKey(1500000, Scalers.ENERGY));
    public static final RegistryObject<CapacitorKey> ENHANCED_ALLOY_SMELTER_ENERGY_TRANSFER = CAPACITOR_KEYS.register("enhanced_alloy_smelter_transfer", () -> new CapacitorKey(180, Scalers.ENERGY));
    public static final RegistryObject<CapacitorKey> ENHANCED_ALLOY_SMELTER_ENERGY_CONSUME = CAPACITOR_KEYS.register("enhanced_alloy_smelter_consume", () -> new CapacitorKey(45, Scalers.ENERGY));

    // Development entries for new/wip machines.
    public static final RegistryObject<CapacitorKey> DEV_ENERGY_CAPACITY = CAPACITOR_KEYS.register("dev_capacity", () -> new CapacitorKey(100000, Scalers.ENERGY));
    public static final RegistryObject<CapacitorKey> DEV_ENERGY_TRANSFER = CAPACITOR_KEYS.register("dev_transfer", () -> new CapacitorKey(120, Scalers.ENERGY));
    public static final RegistryObject<CapacitorKey> DEV_ENERGY_CONSUME = CAPACITOR_KEYS.register("dev_consume", () -> new CapacitorKey(30, Scalers.ENERGY));

    public static void classload() {
        CAPACITOR_KEYS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
