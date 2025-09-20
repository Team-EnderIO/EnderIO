package com.enderio.machines.common.init;

import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.datamap.RangeExtender;
import com.enderio.machines.common.datamap.VatReagent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID)
public class MachineDataMaps {
    @SubscribeEvent
    public static void registerDataMap(RegisterDataMapTypesEvent event) {
        event.register(VatReagent.DATA_MAP);
        event.register(RangeExtender.DATA_MAP);
    }
}
