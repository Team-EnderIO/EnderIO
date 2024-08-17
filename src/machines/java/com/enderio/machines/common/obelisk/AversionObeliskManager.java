package com.enderio.machines.common.obelisk;

import com.enderio.EnderIO;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.blockentity.AversionObeliskBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber(modid = EnderIO.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AversionObeliskManager extends ObeliskManager<AversionObeliskBlockEntity> {
    public static LazyOptional<IObeliskManagerCapability<AversionObeliskBlockEntity>> getManager(ServerLevel serverLevel) {
        return serverLevel.getCapability(ObeliskManagerAttacher.AVERSION_OBELISK_MANAGER_CAPABILITY);
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
        getManager(level).ifPresent(obeliskManager -> {
            var pos = new BlockPos((int) event.getX(), (int) event.getY(), (int) event.getZ());

            Set<AversionObeliskBlockEntity> obelisks = obeliskManager.getObelisksFor(pos);
            if (obelisks == null || obelisks.isEmpty()) {
                return;
            }

            for (AversionObeliskBlockEntity obelisk : obelisks) {
                if (obelisk.handleSpawnEvent(event)) {
                    break;
                }
            }
        });
    }
}
