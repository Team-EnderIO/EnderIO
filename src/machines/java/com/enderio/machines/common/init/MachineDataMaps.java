package com.enderio.machines.common.init;

import com.enderio.machines.common.datamap.VatReagent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MachineDataMaps {
    @SubscribeEvent
    public static void registerDataMap(RegisterDataMapTypesEvent event) {
        event.register(VatReagent.DATA_MAP);
    }
}
