package com.enderio.machines.common.integrations;

import com.enderio.base.api.farm.FarmTaskManager;
import com.enderio.base.api.integration.Integration;
import com.enderio.machines.common.blocks.base.task.MachineFarmingTasks;
import com.enderio.machines.common.blocks.travel_anchor.TravelAnchorBlock;
import net.minecraft.world.entity.player.Player;

public class EnderIOMachinesSelfIntegration implements Integration {

    public static final EnderIOMachinesSelfIntegration INSTANCE = new EnderIOMachinesSelfIntegration();

    @Override
    public boolean canBlockTeleport(Player player) {
        return player.level().getBlockState(player.blockPosition().below()).getBlock() instanceof TravelAnchorBlock;
    }

    @Override
    public void registerFarmTasks(FarmTaskManager manager) {
        manager.addTask(FarmTaskManager.PLANT, MachineFarmingTasks.PLANT_CROP);
        manager.addTask(FarmTaskManager.PLANT, MachineFarmingTasks.PLANT_BLOCK);
        manager.addTask(FarmTaskManager.PLANT, MachineFarmingTasks.PLANT_SAPLING);
        manager.addTask(FarmTaskManager.PLANT, MachineFarmingTasks.PLANT_NETHER_WART);
        manager.addTask(FarmTaskManager.BONEMEAL, MachineFarmingTasks.BONEMEAL);
        manager.addTask(FarmTaskManager.HARVEST, MachineFarmingTasks.HARVEST_CROP);
        manager.addTask(FarmTaskManager.HARVEST, MachineFarmingTasks.HARVEST_FLOWER);
        manager.addTask(FarmTaskManager.HARVEST, MachineFarmingTasks.HARVEST_PITCHER);
        manager.addTask(FarmTaskManager.HARVEST, MachineFarmingTasks.HARVEST_STEM_CROPS);
        manager.addTask(FarmTaskManager.HARVEST, MachineFarmingTasks.HARVEST_BLOCK);
        manager.addTask(FarmTaskManager.HARVEST, MachineFarmingTasks.HARVEST_TREE);
        manager.addTask(FarmTaskManager.HARVEST, MachineFarmingTasks.HARVEST_NETHER_WART);
    }
}
