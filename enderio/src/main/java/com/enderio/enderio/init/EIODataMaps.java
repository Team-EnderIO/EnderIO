package com.enderio.enderio.init;

import com.enderio.enderio.foundation.datamap.RangeExtender;
import com.enderio.enderio.foundation.datamap.VatReagent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber
public class EIODataMaps {
    @SubscribeEvent
    public static void registerDataMap(RegisterDataMapTypesEvent event) {
        event.register(VatReagent.DATA_MAP);
        event.register(RangeExtender.DATA_MAP);
    }
}
