package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SpawnTask extends PoweredTask{

    private static final int maxEntities = 2;
    public static final int maxSpawners = 2;
    public static final int spawnTries = 10;
    private boolean complete;
    private int energyCost;
    private int progress;
    private int maxProgress = 40;
    private final PoweredSpawnerBlockEntity blockEntity;

    /**
     * Create a new powered task.
     *
     * @param energyStorage The energy storage used to power the task.
     */
    public SpawnTask(PoweredSpawnerBlockEntity blockEntity, IMachineEnergyStorage energyStorage) {
        super(energyStorage);
        this.blockEntity = blockEntity;
    }

    @Override
    public void tick() {
        if (progress >= maxProgress) {
            complete = true;
        }

        if (complete) {
            if (isAreaClear() && energyCost == energyStorage.consumeEnergy(energyCost, true)) {
                trySpawnEntity(blockEntity.getBlockPos(), (ServerLevel) blockEntity.getLevel());
                complete = false;
                progress = 0;
            }
        }

        progress ++;
    }

    @Override
    public float getProgress() {
        return progress / maxProgress;
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("Progress", progress);
        nbt.putBoolean("Complete", complete);
        nbt.putInt("EnergyCost", energyCost);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        progress = nbt.getInt("Progress");
        complete = nbt.getBoolean("Complete");
        energyCost = nbt.getInt("EnergyCost");

    }

    /**
     * Check if the area has less spawners and mobs than the config.
     * @return
     */
    public boolean isAreaClear() {
        AABB range = blockEntity.getRange();
        List<? extends Entity> entities = blockEntity.getLevel().getEntities(blockEntity.getEntityType(), range, p -> true);
        if (entities.size() >= maxEntities) { //TODO config? Max amount of entities.
            return false;
        }
        if (BlockPos.betweenClosedStream(range).filter(pos -> blockEntity.getLevel().getBlockEntity(pos) instanceof PoweredSpawnerBlockEntity).count() >= maxSpawners) { //TODO config? Max amount of spawners.
            return false;
        }
        return true;
    }

    public void trySpawnEntity(BlockPos pos, ServerLevel level) {
        for (int i = 0; i < spawnTries; i++) {
            RandomSource randomsource = level.getRandom();
            double x = pos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double)this.blockEntity.getRange().getXsize() + 0.5D;
            double y = pos.getY() + randomsource.nextInt(3) - 1;
            double z = pos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double)this.blockEntity.getRange().getZsize() + 0.5D;
            if (level.noCollision(blockEntity.getEntityType().getAABB(x, y, z))) {

                Entity entity = EntityType.loadEntityRecursive(blockEntity.getEntityData().getEntityTag(), level, entity1 -> {
                    entity1.moveTo(x, y, z, entity1.getYRot(), entity1.getXRot());
                    return entity1;
                });

                if (entity == null) { //TODO Make default spawn tag
                    break;
                }

                if (entity instanceof Mob) {
                    Mob mob = (Mob)entity;
                    net.minecraftforge.eventbus.api.Event.Result res = net.minecraftforge.event.ForgeEventFactory.canEntitySpawn(mob, level, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), null, MobSpawnType.SPAWNER);
                    if (res == net.minecraftforge.eventbus.api.Event.Result.DENY) return;
                }

                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                    return;
                }

                level.levelEvent(2004, pos, 0);
                level.gameEvent(entity, GameEvent.ENTITY_PLACE, new BlockPos(x, y, z));
                if (entity instanceof Mob) {
                    ((Mob)entity).spawnAnim();
                }

                energyStorage.consumeEnergy(energyCost, false);
            }
        }
    }
}
