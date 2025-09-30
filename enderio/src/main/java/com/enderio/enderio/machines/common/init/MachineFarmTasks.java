package com.enderio.enderio.machines.common.init;

import com.enderio.enderio.api.farm.FarmTaskType;
import com.enderio.enderio.api.farm.RegisterFarmTasksEvent;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.BonemealFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.HarvestBlockFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.HarvestCropFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.HarvestFlower;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.HarvestNetherWartFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.HarvestPitcherFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.HarvestStemCropsFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.HarvestTreeFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.PlantBlockFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.PlantCropFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.PlantNetherWartFarmTask;
import com.enderio.enderio.machines.common.blocks.farming_station.tasks.PlantSaplingFarmTask;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class MachineFarmTasks {
    @SubscribeEvent
    public static void register(RegisterFarmTasksEvent event) {
        event.register(FarmTaskType.PLANT, PlantCropFarmTask.INSTANCE);
        event.register(FarmTaskType.PLANT, PlantBlockFarmTask.INSTANCE);
        event.register(FarmTaskType.PLANT, PlantSaplingFarmTask.INSTANCE);
        event.register(FarmTaskType.PLANT, PlantNetherWartFarmTask.INSTANCE);
        event.register(FarmTaskType.FERTILIZE, BonemealFarmTask.INSTANCE);
        event.register(FarmTaskType.HARVEST, HarvestCropFarmTask.INSTANCE);
        event.register(FarmTaskType.HARVEST, HarvestPitcherFarmTask.INSTANCE);
        event.register(FarmTaskType.HARVEST, HarvestFlower.INSTANCE);
        event.register(FarmTaskType.HARVEST, HarvestStemCropsFarmTask.INSTANCE);
        event.register(FarmTaskType.HARVEST, HarvestBlockFarmTask.INSTANCE);
        event.register(FarmTaskType.HARVEST, HarvestTreeFarmTask.INSTANCE);
        event.register(FarmTaskType.HARVEST, HarvestNetherWartFarmTask.INSTANCE);
    }
}
