package com.enderio.enderio.init;

import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.foundation.datamap.RangeExtender;
import com.enderio.enderio.foundation.datamap.VatReagent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.datamaps.RegisterDataMapTypesEvent;

@Mod.EventBusSubscriber
public class EIODataMaps {
    @SubscribeEvent
    public static void registerDataMap(RegisterDataMapTypesEvent event) {
        event.register(VatReagent.DATA_MAP);
        event.register(RangeExtender.DATA_MAP);
        event.register(GrindingBallData.DATA_MAP_TYPE);
    }
}
