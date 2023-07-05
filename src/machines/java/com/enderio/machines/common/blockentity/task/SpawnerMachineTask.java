package com.enderio.machines.common.blockentity.task;

import com.enderio.machines.common.blockentity.PoweredSpawnerBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.enderio.machines.common.tag.MachineTags;
import com.mojang.serialization.DataResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SpawnerMachineTask implements IPoweredMachineTask {

    public static final int spawnTries = 10;
    private boolean complete;
    private int energyCost;
    private int energyConsumed = 0;
    private float efficiency = 1;
    private SpawnType spawnType = MachinesConfig.COMMON.SPAWN_TYPE.get();
    @Nullable
    private EntityType<? extends Entity> entityType;

    private final PoweredSpawnerBlockEntity blockEntity;
    private final IMachineEnergyStorage energyStorage;

    /**
     * Create a new powered task.
     *
     * @param energyStorage The energy storage used to power the task.
     */
    public SpawnerMachineTask(PoweredSpawnerBlockEntity blockEntity, IMachineEnergyStorage energyStorage, Optional<ResourceLocation> rl) {
        this.blockEntity = blockEntity;
        this.energyStorage = energyStorage;
        loadSoulData(rl);
    }

    @Override
    public IMachineEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void tick() {
        if (entityType == null) {
            complete = true;
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
    public boolean isCompleted() {
        return complete;
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
    
    private void loadSoulData(Optional<ResourceLocation> rl) {
        if (rl.isEmpty()) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
            return;
        }
        Optional<Holder.Reference<EntityType<?>>> optionalEntity = ForgeRegistries.ENTITY_TYPES.getDelegate(rl.get());
        if (optionalEntity.isEmpty() || ! ForgeRegistries.ENTITY_TYPES.getKey(optionalEntity.get().get()).equals(rl.get())) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
            return;
        }
        if (optionalEntity.get().is(MachineTags.EntityTypes.SPAWNER_BLACKLIST)) {
            return;
        }
        Optional<SpawnerSoul.SoulData> opData = SpawnerSoul.SPAWNER.matches(rl.get());
        if (opData.isEmpty()) { //Fallback
            this.entityType = optionalEntity.get().get();
            this.energyCost = 4000;
            if (entityType.create(this.blockEntity.getLevel()) instanceof LivingEntity entity) { //Are we 100% guaranteed this is a living entity?
                this.energyCost += entity.getMaxHealth()*50; //TODO actually balance based on health
            }
            return;
        }
        SpawnerSoul.SoulData data = opData.get();
        this.entityType = optionalEntity.get().get();
        this.energyCost = data.power();
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
                switch (spawnType) {
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

                if (entity == null) {
                    blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKOWN_MOB);
                    break;
                }

                if (entity instanceof Mob mob) { // based on vanilla spawner
                    MobSpawnEvent.FinalizeSpawn event = ForgeEventFactory.onFinalizeSpawnSpawner(mob, level, level.getCurrentDifficultyAt(pos), null,  blockEntity.getEntityData().getEntityTag(), null);
                    if (event == null || event.isSpawnCancelled()) {
                        blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                        return false;
                    } else {
                        ForgeEventFactory.onFinalizeSpawn(mob, level, event.getDifficulty(), event.getSpawnType(), event.getSpawnData(), event.getSpawnTag());
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

    // region Serialization

    private static final String KEY_ENERGY_CONSUMED = "EnergyConsumed";

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(KEY_ENERGY_CONSUMED, energyConsumed);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energyConsumed = nbt.getInt(KEY_ENERGY_CONSUMED);
    }

    // endregion

    // TODO: Might want to move this to its own file in future.
    public enum SpawnType {
        ENTITYTYPE("entitytype"),
        COPY("copy");

        private final String name;

        SpawnType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static DataResult<SpawnType> byName(String pTranslationKey) {
            for(SpawnType type : values()) {
                if (type.name.equals(pTranslationKey)) {
                    return DataResult.success(type);
                }
            }
            return DataResult.error(()->"unkown type");
        }
    }
}
