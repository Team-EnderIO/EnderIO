package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class SpawnTask extends PoweredTask{

    public static final int maxSpawners = 2;
    public static final int spawnTries = 10;
    private boolean complete;
    private int energyCost = 40000;
    private int energyConsumed = 0;
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
        if (energyConsumed >= energyCost) {
            if (isAreaClear()) {
                complete = trySpawnEntity(blockEntity.getBlockPos(), (ServerLevel) blockEntity.getLevel()); //ready to spawn but blocked for a reason.
            }
        } else {
            energyConsumed += energyStorage.consumeEnergy(energyCost - energyConsumed, false);
        }

    }

    @Override
    public float getProgress() {
        return energyConsumed / ((float)energyCost);
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("EnergyConsumed", energyConsumed);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energyConsumed = nbt.getInt("EnergyConsumed");
    }

    /**
     * Check if the area has less spawners and mobs than the config.
     * @return
     */
    public boolean isAreaClear() {
        AABB range = new AABB(blockEntity.getBlockPos()).inflate(blockEntity.getRange());
        List<? extends Entity> entities = blockEntity.getLevel().getEntities(blockEntity.getEntityType(), range, p -> p instanceof LivingEntity);
        if (entities.size() >= MachinesConfig.COMMON.MAX_SPAWNER_ENTITIES.get()) {
            return false;
        }
        if (BlockPos.betweenClosedStream(range).filter(pos -> blockEntity.getLevel().getBlockEntity(pos) instanceof PoweredSpawnerBlockEntity).count() >= maxSpawners) { //TODO config? Max amount of spawners.
            return false;
        }
        return true;
    }

    public boolean trySpawnEntity(BlockPos pos, ServerLevel level) {
        for (int i = 0; i < spawnTries; i++) {
            RandomSource randomsource = level.getRandom();
            double x = pos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double)this.blockEntity.getRange() + 0.5D;
            double y = pos.getY() + randomsource.nextInt(3) - 1;
            double z = pos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double)this.blockEntity.getRange() + 0.5D;
            if (level.noCollision(blockEntity.getEntityType().getAABB(x, y, z))) {

                Entity entity = null;
                if (MachinesConfig.COMMON.SPAWN_TYPE.get() == SpawnType.COPY) {
                    entity = EntityType.loadEntityRecursive(blockEntity.getEntityData().getEntityTag(), level, entity1 -> {
                        entity1.moveTo(x, y, z, entity1.getYRot(), entity1.getXRot());
                        return entity1;
                    });
                } else {
                    Optional<EntityType<?>> id = Registry.ENTITY_TYPE.getOptional(new ResourceLocation(blockEntity.getEntityData().getEntityTag().getString("id")));
                    if (id.isPresent()) {
                        entity = id.get().create(level);
                        entity.moveTo(x, y, z);
                    }
                }


                if (entity == null) { //TODO Make default spawn tag?
                    break;
                }

                if (entity instanceof Mob mob) {
                    net.minecraftforge.eventbus.api.Event.Result res = net.minecraftforge.event.ForgeEventFactory.canEntitySpawn(mob, level, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), null, MobSpawnType.SPAWNER);
                    if (res == net.minecraftforge.eventbus.api.Event.Result.DENY) return false;
                }

                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                    return false;
                }

                level.levelEvent(2004, pos, 0);
                level.gameEvent(entity, GameEvent.ENTITY_PLACE, new BlockPos(x, y, z));
                if (entity instanceof Mob mob) {
                    mob.spawnAnim();
                }

                //Clear energy after spawn
                energyConsumed = 0;
                return true;
            }
        }
        return false;
    }

    public enum SpawnType {
        ENTITYTYPE("entitytype"),
        COPY("copy");

        private final String name;

        SpawnType(String name) {
            this.name = name;
        }
    }
}
