package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.machines.common.config.MachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.List;
import java.util.UUID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class MobSpawnTask extends PoweredSpawnerTask {

    private float efficiency = 1;

    public MobSpawnTask(PoweredSpawnerBlockEntity blockEntity) {
        super(PoweredSpawnerMode.SPAWN, blockEntity);
    }

    public MobSpawnTask(PoweredSpawnerBlockEntity blockEntity, int energyCost, EntityType<? extends Entity> entityType,
            MobSpawnMode spawnMode) {
        super(PoweredSpawnerMode.SPAWN, blockEntity, energyCost, entityType, spawnMode);
    }

    @Override
    protected void onTaskCompleted() {
        if (isAreaClear()) {
            trySpawnEntity(blockEntity.getBlockPos(), (ServerLevel) blockEntity.getLevel());
        }
    }

    /**
     * Check if the area has fewer spawners and mobs than the config.
     * @return
     */
    private boolean isAreaClear() {
        AABB range = new AABB(blockEntity.getBlockPos()).inflate(blockEntity.getRange());

        List<? extends Entity> entities = blockEntity.getLevel()
                .getEntities(entityType(), range, p -> p instanceof LivingEntity);

        if (entities.size() >= MachinesConfig.COMMON.MAX_SPAWNER_ENTITIES.get()) {
            setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.TOO_MANY_MOB);
            return false;
        }

        long count = BlockPos.betweenClosedStream(range)
                .filter(pos -> blockEntity.getLevel().getBlockEntity(pos) instanceof PoweredSpawnerBlockEntity)
                .count();
        if (count >= MachinesConfig.COMMON.MAX_SPAWNERS.get()) {
            this.efficiency = MachinesConfig.COMMON.MAX_SPAWNERS.get() / (float) count;
        }

        return true;
    }

    public void trySpawnEntity(BlockPos pos, ServerLevel level) {
        if (isCompleted()) {
            return;
        }

        if (this.efficiency < level.random.nextFloat()) {
            setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.TOO_MANY_SPAWNER);
            return;
        }

        for (int i = 0; i < MachinesConfig.COMMON.SPAWN_AMOUNT.get(); i++) {
            RandomSource randomsource = level.getRandom();
            double x = pos.getX()
                    + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) this.blockEntity.getRange()
                    + 0.5D;
            double y = pos.getY() + randomsource.nextInt(3) - 1;
            double z = pos.getZ()
                    + (randomsource.nextDouble() - randomsource.nextDouble()) * (double) this.blockEntity.getRange()
                    + 0.5D;

            if (level.noCollision(entityType().getSpawnAABB(x, y, z))) {
                Entity entity;
                switch (spawnMode()) {
                case COPY -> {
                    // TODO: Stop using BE to get entity tag data...
                    entity = EntityType.loadEntityRecursive(blockEntity.getBoundSoul().getEntityTag(), level,
                            entity1 -> {
                                entity1.moveTo(x, y, z, entity1.getYRot(), entity1.getXRot());
                                entity1.setUUID(UUID.randomUUID());
                                return entity1;
                            });
                }
                case NEW -> {
                    // TODO: should we be using the ctor that accepts a position and spawn type etc.
                    entity = entityType().create(level);
                    if (entity != null) {
                        entity.moveTo(x, y, z);
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + spawnMode());
                }

                if (entity == null) {
                    setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKNOWN_MOB);
                    break;
                }

                if (entity instanceof Mob mob) { // based on vanilla spawner
                    if (blockEntity.hasMindKiller()) {
                        mob.setNoAi(true);
                        mob.getPersistentData().putBoolean("enderio:movable", true);
                    }
                    FinalizeSpawnEvent event = EventHooks.finalizeMobSpawnSpawner(mob, level,
                            level.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null, blockEntity, false);
                    if (event.isSpawnCancelled()) {
                        setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                        continue;
                    } else if(spawnMode() != MobSpawnMode.COPY){
                        EventHooks.finalizeMobSpawn(mob, level, event.getDifficulty(), event.getSpawnType(),
                                event.getSpawnData());
                    }
                }

                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                    setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                    continue;
                }

                level.levelEvent(2004, pos, 0);
                level.gameEvent(entity, GameEvent.ENTITY_PLACE, new BlockPos((int) x, (int) y, (int) z));
                if (entity instanceof Mob mob) {
                    mob.spawnAnim();
                }

                // Successfully spawned!
                setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.NONE);
                isComplete = true;
            }
        }
    }

    //Credit to Shadows-of-Fire for providing this workaround https://github.com/Shadows-of-Fire/Apothic-Spawners/blob/1.21/src/main/java/dev/shadowsoffire/apothic_spawners/ASEvents.java#L120
    @SubscribeEvent
    public static void tickNoAIMobs(EntityTickEvent.Pre e) {
        if (e.getEntity() instanceof Mob mob) {
            if (!mob.level().isClientSide() && mob.isNoAi() && mob.getPersistentData().getBoolean("enderio:movable")) {
                mob.setNoAi(false);
                mob.travel(new Vec3(mob.xxa, mob.yya, mob.zza));
                mob.setNoAi(true);
            }
        }
    }
}
