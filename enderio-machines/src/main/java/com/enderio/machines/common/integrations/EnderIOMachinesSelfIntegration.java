package com.enderio.machines.common.integrations;

import com.enderio.base.api.integration.Integration;
import com.enderio.machines.api.farm.FarmTaskManager;
import com.enderio.machines.common.block.TravelAnchorBlock;
import com.enderio.machines.common.blockentity.task.EIOFarmTasks;
import net.minecraft.world.entity.player.Player;

public class EnderIOMachinesSelfIntegration implements Integration {

    public static final EnderIOMachinesSelfIntegration INSTANCE = new EnderIOMachinesSelfIntegration();

    @Override
    public boolean canBlockTeleport(Player player) {
        return player.level().getBlockState(player.blockPosition().below()).getBlock() instanceof TravelAnchorBlock;
    }

    @Override
    public void registerFarmTasks(FarmTaskManager manager) {
        manager.addTask(FarmTaskManager.PLANT, EIOFarmTasks.PLANT_CROP);
        manager.addTask(FarmTaskManager.PLANT, EIOFarmTasks.PLANT_BLOCK);
        manager.addTask(FarmTaskManager.BONEMEAL, EIOFarmTasks.BONEMEAL);
        manager.addTask(FarmTaskManager.HARVEST, EIOFarmTasks.HARVEST_CROP);
        manager.addTask(FarmTaskManager.HARVEST, EIOFarmTasks.HARVEST_FLOWER);
        manager.addTask(FarmTaskManager.HARVEST, EIOFarmTasks.HARVEST_PITCHER);
        manager.addTask(FarmTaskManager.HARVEST, EIOFarmTasks.HARVEST_STEM_CROPS);
        manager.addTask(FarmTaskManager.HARVEST, EIOFarmTasks.HARVEST_BLOCK);
    }
}
