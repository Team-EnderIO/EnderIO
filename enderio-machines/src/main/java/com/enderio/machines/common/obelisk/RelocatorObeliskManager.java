package com.enderio.machines.common.obelisk;

import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.blocks.obelisks.relocator.RelocatorObeliskBlockEntity;
import com.enderio.machines.common.init.MachineAttachments;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class RelocatorObeliskManager extends ObeliskAreaManager<RelocatorObeliskBlockEntity> {

    public static RelocatorObeliskManager getManager(ServerLevel serverLevel) {
        return serverLevel.getData(MachineAttachments.RELOCATOR_OBELISK_MANAGER);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onSpawnEvent(FinalizeSpawnEvent event) {
        // Only affects natural spawns
        if (event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }

        // If there is no obelisk manager, there is nothing to do.
        ServerLevelAccessor levelAccessor = event.getLevel();
        ServerLevel level = levelAccessor.getLevel();
        if (!level.hasData(MachineAttachments.RELOCATOR_OBELISK_MANAGER)) {
            return;
        }

        var pos = new BlockPos((int) event.getX(), (int) event.getY(), (int) event.getZ());

        var obeliskManager = getManager(level);

        Set<RelocatorObeliskBlockEntity> obelisks = obeliskManager.getObelisksFor(pos);
        if (obelisks == null || obelisks.isEmpty()) {
            return;
        }

        for (RelocatorObeliskBlockEntity obelisk : obelisks) {
            if (obelisk.handleSpawnEvent(event)) {
                break;
            }
        }
    }
}
