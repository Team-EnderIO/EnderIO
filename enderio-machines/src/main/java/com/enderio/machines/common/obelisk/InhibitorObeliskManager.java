package com.enderio.machines.common.obelisk;

import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.blocks.obelisks.inhibitor.InhibitorObeliskBlockEntity;
import com.enderio.machines.common.init.MachineAttachments;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class InhibitorObeliskManager extends ObeliskAreaManager<InhibitorObeliskBlockEntity> {

    public static InhibitorObeliskManager getManager(ServerLevel level) {
        return level.getData(MachineAttachments.INHIBITOR_OBELISK_MANAGER);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onTeleportEvent(EntityTeleportEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // don't work on server commands
        if (event instanceof EntityTeleportEvent.TeleportCommand
                || event instanceof EntityTeleportEvent.SpreadPlayersCommand) {
            return;
        }

        // If there is no obelisk manager, there is nothing to do.
        if (!serverLevel.hasData(MachineAttachments.INHIBITOR_OBELISK_MANAGER)) {
            return;
        }

        var target = new BlockPos((int) event.getTargetX(), (int) event.getTargetY(), (int) event.getTargetZ());

        var obeliskManager = getManager(serverLevel);

        Set<InhibitorObeliskBlockEntity> obelisks = obeliskManager.getObelisksFor(target);
        if (obelisks == null || obelisks.isEmpty()) {
            var prev = new BlockPos((int) event.getPrevX(), (int) event.getPrevY(), (int) event.getPrevZ());
            obelisks = obeliskManager.getObelisksFor(prev);
            if (obelisks == null || obelisks.isEmpty()) {
                return;
            }
        }

        for (InhibitorObeliskBlockEntity obelisk : obelisks) {
            if (obelisk.handleTeleportEvent(event)) {
                break;
            }
        }
    }
}
