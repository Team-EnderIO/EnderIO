package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.enderio.machines.common.tag.MachineTags;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SpawnTask extends PoweredTask{

    public static final int spawnTries = 10;
    private boolean complete;
    private int energyCost;
    private int energyConsumed = 0;
    private final PoweredSpawnerBlockEntity blockEntity;
    private float efficiency = 1;
    private SpawnType spawnType = MachinesConfig.COMMON.SPAWN_TYPE.get();
    @Nullable
    private EntityType<?> entityType;

    /**
     * Create a new powered task.
     *
     * @param energyStorage The energy storage used to power the task.
     */
    public SpawnTask(PoweredSpawnerBlockEntity blockEntity, IMachineEnergyStorage energyStorage, Optional<ResourceLocation> rl) {
        super(energyStorage);
        this.blockEntity = blockEntity;
        loadSoulData(rl);
    }

    @Override
    public void tick() {
        if (entityType == null) {
            return;
        }
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
        Optional<EntityType<?>> entity = Registry.ENTITY_TYPE.getOptional(rl.get());
        if (entity.isEmpty() || !Registry.ENTITY_TYPE.getKey(entity.get()).equals(rl.get())) { // check we don't get the default pig
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
            return false;
        }
        List<? extends Entity> entities = blockEntity.getLevel().getEntities(entity.get(), range, p -> p instanceof LivingEntity);
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
    
    private void loadSoulData(Optional<ResourceLocation> rl) {
        if (rl.isEmpty()) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
            return;
        }
        Optional<EntityType<?>> optionalEntity = Registry.ENTITY_TYPE.getOptional(rl.get());
        if (optionalEntity.isEmpty() || !Registry.ENTITY_TYPE.getKey(optionalEntity.get()).equals(rl.get())) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
            return;
        }
        if (optionalEntity.get().is(MachineTags.EntityTypes.SPAWNER_BLACKLIST)) {
            return;
        }
        Optional<SpawnerSoul.SoulData> opData = SpawnerSoul.SPAWNER.matches(rl.get());
        if (opData.isEmpty()) { //Fallback
            this.entityType = optionalEntity.get();
            this.energyCost = 40000;
            return;
        }
        SpawnerSoul.SoulData data = opData.get();
        this.entityType = optionalEntity.get();
        this.energyCost =data.power();
        this.spawnType = data.spawnType();
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


            if (level.noCollision(this.entityType.getAABB(x, y, z))) {

                Entity entity = null;
                switch (spawnType) {
                    case COPY -> {
                        entity = EntityType.loadEntityRecursive(blockEntity.getEntityData().getEntityTag(), level, entity1 -> {
                            entity1.moveTo(x, y, z, entity1.getYRot(), entity1.getXRot());
                            return entity1;
                        });
                    }
                    case ENTITYTYPE -> {
                            entity = this.entityType.create(level);
                            entity.moveTo(x, y, z);
                    }
                }

                if (entity == null) { //TODO Make default spawn tag?
                    blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
                    break;
                }

                if (entity instanceof Mob mob) {
                    net.minecraftforge.eventbus.api.Event.Result res = net.minecraftforge.event.ForgeEventFactory.canEntitySpawn(mob, level, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ(), null, MobSpawnType.SPAWNER);
                    if (res == net.minecraftforge.eventbus.api.Event.Result.DENY) {
                        blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                        return false;
                    }
                }

                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                    blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                    return false;
                }

                level.levelEvent(2004, pos, 0);
                level.gameEvent(entity, GameEvent.ENTITY_PLACE, new BlockPos(x, y, z));
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

        public static DataResult<SpawnType> byName(String pTranslationKey) {
            for(SpawnType type : values()) {
                if (type.name.equals(pTranslationKey)) {
                    return DataResult.success(type);
                }
            }
            return DataResult.error("unkown type");
        }
    }
}
