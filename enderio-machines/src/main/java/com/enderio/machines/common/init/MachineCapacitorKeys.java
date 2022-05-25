package com.enderio.machines.common.init;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.capacitor.Scalers;
import com.enderio.base.EnderIO;
import com.enderio.machines.EIOMachines;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MachineCapacitorKeys {
    private static final DeferredRegister<CapacitorKey> CAPACITOR_KEYS = DeferredRegister.create(EnderIO.CAPACITOR_KEY_REGISTRY_KEY, EIOMachines.MODID);

    // TODO: If this is used, one day all values should be turned to configs

    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_CAPACITY = CAPACITOR_KEYS.register("alloy_smelter_capacity", () -> new CapacitorKey(100000, Scalers.LINEAR_2_MINUS_1));
    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_TRANSFER = CAPACITOR_KEYS.register("alloy_smelter_transfer", () -> new CapacitorKey(120, Scalers.LINEAR));
    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_CONSUME = CAPACITOR_KEYS.register("alloy_smelter_consume", () -> new CapacitorKey(30, Scalers.LINEAR));

    public static void classload() {
        CAPACITOR_KEYS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
