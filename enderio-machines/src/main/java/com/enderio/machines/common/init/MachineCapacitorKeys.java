package com.enderio.machines.common.init;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.capacitor.PowerCapacitorKey;
import com.enderio.base.EnderIO;
import com.enderio.machines.EIOMachines;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MachineCapacitorKeys {
    private static final DeferredRegister<CapacitorKey> CAPACITOR_KEYS = DeferredRegister.create(EnderIO.CAPACITOR_KEY_REGISTRY_KEY, EIOMachines.MODID);

    // TODO: If this is used, one day all values should be turned to configs

    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_CAPACITY = CAPACITOR_KEYS.register("alloy_smelter_capacity", () -> new PowerCapacitorKey(100000));
    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_TRANSFER = CAPACITOR_KEYS.register("alloy_smelter_transfer", () -> new PowerCapacitorKey(120));
    public static final RegistryObject<CapacitorKey> ALLOY_SMELTER_ENERGY_CONSUME = CAPACITOR_KEYS.register("alloy_smelter_consume", () -> new PowerCapacitorKey(30));

    public static void classload() {
        CAPACITOR_KEYS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
