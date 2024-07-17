package com.enderio.machines.common.obelisk;

import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.blockentity.AversionObeliskBlockEntity;
import com.enderio.machines.common.init.MachineAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

import java.util.Set;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class AversionObeliskManager extends ObeliskAreaManager<AversionObeliskBlockEntity> {

    public static AversionObeliskManager getManager(ServerLevel level) {
        return level.getData(MachineAttachments.AVERSION_OBELISK_MANAGER);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onSpawnEvent(FinalizeSpawnEvent event) {
        if (event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }

        ServerLevelAccessor levelAccessor = event.getLevel();
        var pos = new BlockPos((int)event.getX(), (int)event.getY(), (int)event.getZ());

        var obeliskManager = getManager(levelAccessor.getLevel());

        Set<AversionObeliskBlockEntity> obelisks = obeliskManager.getObelisksFor(pos);

        if (obelisks == null || obelisks.isEmpty()) {
            return;
        }

        for (AversionObeliskBlockEntity obelisk : obelisks) {
            if (obelisk.handleSpawnEvent(event)) {
                break;
            }
        }
    }
}
