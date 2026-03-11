package com.enderio.enderio.content.machines.obelisks.relocator;

import com.enderio.enderio.content.machines.obelisks.ObeliskAreaManager;
import com.enderio.enderio.init.EIOAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.MobSpawnEvent;

import java.util.Set;

@Mod.EventBusSubscriber
public class RelocatorObeliskManager extends ObeliskAreaManager<RelocatorObeliskBlockEntity> {

    public static RelocatorObeliskManager getManager(ServerLevel serverLevel) {
        return serverLevel.getData(EIOAttachments.RELOCATOR_OBELISK_MANAGER);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onSpawnEvent(MobSpawnEvent.FinalizeSpawn event) {
        // Only affects natural spawns
        if (event.getSpawnType() != MobSpawnType.NATURAL) {
            return;
        }

        // If there is no obelisk manager, there is nothing to do.
        ServerLevelAccessor levelAccessor = event.getLevel();
        ServerLevel level = levelAccessor.getLevel();
        if (!level.hasData(EIOAttachments.RELOCATOR_OBELISK_MANAGER)) {
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
