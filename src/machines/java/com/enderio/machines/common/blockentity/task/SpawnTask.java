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
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class SpawnTask extends PoweredTask{

    public static final int spawnTries = 10;
    private boolean complete;
    private int energyCost = 40000;//TODO Custom Config/Json File
    private int energyConsumed = 0;
    private final PoweredSpawnerBlockEntity blockEntity;
    private float efficiency = 1;

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
     * Check if the area has fewer spawners and mobs than the config.
     * @return
     */
    public boolean isAreaClear() {
        AABB range = new AABB(blockEntity.getBlockPos()).inflate(blockEntity.getRange());
        Optional<ResourceLocation> rl = blockEntity.getEntityType();
        if (rl.isEmpty()) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
            return false;
        }
        EntityType<?> entity = ForgeRegistries.ENTITY_TYPES.getValue(rl.get());
        if (entity == null || !ForgeRegistries.ENTITY_TYPES.getKey(entity).equals(rl.get())) { // check we don't get the default pig
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
            return false;
        }
        List<? extends Entity> entities = blockEntity.getLevel().getEntities(entity, range, p -> p instanceof LivingEntity);
        if (entities.size() >= MachinesConfig.COMMON.MAX_SPAWNER_ENTITIES.get()) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.TOO_MANY_MOB);
            return false;
        }
        long count = BlockPos.betweenClosedStream(range).filter(pos -> blockEntity.getLevel().getBlockEntity(pos) instanceof PoweredSpawnerBlockEntity).count();
        if (count >= MachinesConfig.COMMON.MAX_SPAWNERS.get()) {
            this.efficiency = MachinesConfig.COMMON.MAX_SPAWNERS.get()/(float)count;
        }
        return true;
    }

    public boolean trySpawnEntity(BlockPos pos, ServerLevel level) {
        if (this.efficiency < level.random.nextFloat()) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.TOO_MANY_SPAWNER);
            return false;
        }
        for (int i = 0; i < spawnTries; i++) {
            RandomSource randomsource = level.getRandom();
            double x = pos.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double)this.blockEntity.getRange() + 0.5D;
            double y = pos.getY() + randomsource.nextInt(3) - 1;
            double z = pos.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * (double)this.blockEntity.getRange() + 0.5D;
            Optional<ResourceLocation> rl = blockEntity.getEntityType();
            if (rl.isEmpty()) {
                blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
                return false;
            }
            EntityType<?> optionalEntity = ForgeRegistries.ENTITY_TYPES.getValue(rl.get());
            if (optionalEntity == null || !ForgeRegistries.ENTITY_TYPES.getKey(optionalEntity).equals(rl.get())) { // check we don't get the default pig
                blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
                return false;
            }
            if (level.noCollision(optionalEntity.getAABB(x, y, z))) {

                Entity entity = null;
                switch (MachinesConfig.COMMON.SPAWN_TYPE.get()) {
                    case COPY -> {
                        entity = EntityType.loadEntityRecursive(blockEntity.getEntityData().getEntityTag(), level, entity1 -> {
                            entity1.moveTo(x, y, z, entity1.getYRot(), entity1.getXRot());
                            return entity1;
                        });
                    }
                    case ENTITYTYPE -> {
                        EntityType<?> id = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(blockEntity.getEntityData().getEntityTag().getString("id")));
                        if (id != null) {
                            entity = id.create(level);
                            entity.moveTo(x, y, z);
                        }
                    }
                }

                if (entity == null) { //TODO Make default spawn tag?
                    blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
                    break;
                }

                if (entity instanceof Mob mob) {
                    SpawnGroupData res = net.minecraftforge.event.ForgeEventFactory.onFinalizeSpawn(mob, level, level.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null, null);
                    if (res == null) {
                        blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                        return false;
                    }
                }

                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                    blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                    return false;
                }

                level.levelEvent(2004, pos, 0);
                level.gameEvent(entity, GameEvent.ENTITY_PLACE, new BlockPos((int) x, (int) y, (int) z));
                if (entity instanceof Mob mob) {
                    mob.spawnAnim();
                }

                //Clear energy after spawn
                energyConsumed = 0;
                blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.NONE);
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
