package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.blocks.base.task.PoweredMachineTask;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.souldata.SpawnerSoul;
import com.enderio.machines.common.tag.MachineTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID)
public class SpawnerMachineTask implements PoweredMachineTask {

    private boolean complete;
    private int energyCost;
    private int energyConsumed = 0;
    private float efficiency = 1;
    private SpawnType spawnType = MachinesConfig.COMMON.SPAWN_TYPE.get();
    @Nullable
    private EntityType<? extends Entity> entityType;
    private static boolean reload = false;
    private boolean reloadCache = reload;
    private final PoweredSpawnerBlockEntity blockEntity;
    private final IMachineEnergyStorage energyStorage;

    /**
     * Create a new powered task.
     *
     * @param energyStorage The energy storage used to power the task.
     */
    public SpawnerMachineTask(PoweredSpawnerBlockEntity blockEntity, IMachineEnergyStorage energyStorage,
            @Nullable ResourceLocation rl) {
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
            if (reload != reloadCache) {
                reloadCache = reload;
                complete = true;
            }
            return;
        }
        if (energyConsumed >= energyCost) {
            if (isAreaClear()) {
                complete = trySpawnEntity(blockEntity.getBlockPos(), (ServerLevel) blockEntity.getLevel()); // ready to
                                                                                                            // spawn but
                                                                                                            // blocked
                                                                                                            // for a
                                                                                                            // reason.
            }
        } else {
            energyConsumed += energyStorage.consumeEnergy(energyCost - energyConsumed, false);
        }

    }

    @Override
    public float getProgress() {
        return energyConsumed / ((float) energyCost);
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
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKNOWN_MOB);
            return false;
        }
        EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.get(rl.get());
        if (!BuiltInRegistries.ENTITY_TYPE.getKey(entity).equals(rl.get())) { // check we don't get the default pig
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKNOWN_MOB);
            return false;
        }
        List<? extends Entity> entities = blockEntity.getLevel()
                .getEntities(entity, range, p -> p instanceof LivingEntity);
        if (entities.size() >= MachinesConfig.COMMON.MAX_SPAWNER_ENTITIES.get()) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.TOO_MANY_MOB);
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

    private void loadSoulData(@Nullable ResourceLocation rl) {
        if (rl == null) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKNOWN_MOB);
            return;
        }

        Optional<Holder.Reference<EntityType<?>>> optionalEntity = BuiltInRegistries.ENTITY_TYPE
                .getHolder(ResourceKey.create(Registries.ENTITY_TYPE, rl));
        if (optionalEntity.isEmpty()
                || !BuiltInRegistries.ENTITY_TYPE.getKey(optionalEntity.get().value()).equals(rl)) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKNOWN_MOB);
            return;
        }

        if (optionalEntity.get().value().is(MachineTags.EntityTypes.SPAWNER_BLACKLIST)) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.DISABLED);
            return;
        }

        Optional<SpawnerSoul.SoulData> opData = SpawnerSoul.SPAWNER.matches(rl);
        if (opData.isEmpty()) { // Fallback
            this.entityType = optionalEntity.get().value();
            this.energyCost = 50000;
            if (entityType.create(this.blockEntity.getLevel()) instanceof LivingEntity entity) { // Are we 100%
                                                                                                 // guaranteed this is a
                                                                                                 // living entity?
                this.energyCost += (int) entity.getMaxHealth() * 50; // TODO actually balance based on health
            }
            return;
        }

        SpawnerSoul.SoulData data = opData.get();
        this.entityType = optionalEntity.get().value();
        this.energyCost = data.power();
        this.spawnType = data.spawnType();
    }

    public boolean trySpawnEntity(BlockPos pos, ServerLevel level) {
        boolean spawned = false;
        if (this.efficiency < level.random.nextFloat()) {
            blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.TOO_MANY_SPAWNER);
            return false;
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

            Optional<ResourceLocation> rl = blockEntity.getEntityType();
            if (rl.isEmpty()) {
                blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKNOWN_MOB);
                return false;
            }
            EntityType<?> optionalEntity = BuiltInRegistries.ENTITY_TYPE.get(rl.get());
            if (!BuiltInRegistries.ENTITY_TYPE.getKey(optionalEntity).equals(rl.get())) { // check we don't get the
                                                                                          // default pig
                blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKNOWN_MOB);
                return false;
            }
            if (level.noCollision(optionalEntity.getSpawnAABB(x, y, z))) {

                Entity entity = null;
                switch (spawnType) {
                case COPY -> {
                    entity = EntityType.loadEntityRecursive(blockEntity.getEntityData().getEntityTag(), level,
                            entity1 -> {
                                entity1.moveTo(x, y, z, entity1.getYRot(), entity1.getXRot());
                                return entity1;
                            });
                }
                case ENTITY_TYPE -> {
                    EntityType<?> id = BuiltInRegistries.ENTITY_TYPE
                            .get(ResourceLocation.parse(blockEntity.getEntityData().getEntityTag().getString("id")));
                    if (id != null) {
                        entity = id.create(level);
                        entity.moveTo(x, y, z);
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: " + spawnType);
                }

                if (entity == null) {
                    blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.UNKNOWN_MOB);
                    break;
                }

                if (entity instanceof Mob mob) { // based on vanilla spawner
                    FinalizeSpawnEvent event = EventHooks.finalizeMobSpawnSpawner(mob, level,
                            level.getCurrentDifficultyAt(pos), MobSpawnType.SPAWNER, null, blockEntity, false);
                    if (event.isSpawnCancelled()) {
                        blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                        continue;
                    } else {
                        EventHooks.finalizeMobSpawn(mob, level, event.getDifficulty(), event.getSpawnType(),
                                event.getSpawnData());
                    }
                }

                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                    blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OTHER_MOD);
                    continue;
                }

                level.levelEvent(2004, pos, 0);
                level.gameEvent(entity, GameEvent.ENTITY_PLACE, new BlockPos((int) x, (int) y, (int) z));
                if (entity instanceof Mob mob) {
                    mob.spawnAnim();
                }

                spawned = true;
                blockEntity.setReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.NONE);
            }
        }

        if (spawned) {
            // Clear energy after spawn
            energyConsumed -= energyCost; // The same amount of energy is used for 1 or
                                          // MachinesConfig.COMMON.SPAWN_AMOUNT.get() spawns, so make sure your spawner
                                          // has enough valid spaces!
        }

        return spawned;
    }

    // region Serialization

    private static final String KEY_ENERGY_CONSUMED = "EnergyConsumed";

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt(KEY_ENERGY_CONSUMED, energyConsumed);
        return nbt;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        energyConsumed = nbt.getInt(KEY_ENERGY_CONSUMED);
    }

    // endregion

    @SubscribeEvent
    static void reloadTags(TagsUpdatedEvent event) {
        reload = !reload;
    }

    // TODO: Might want to move this to its own file in future.
    public enum SpawnType implements StringRepresentable {
        ENTITY_TYPE(0, "entity_type"), COPY(1, "copy");

        public static final Codec<SpawnType> CODEC = StringRepresentable.fromEnum(SpawnType::values);
        public static final IntFunction<SpawnType> BY_ID = ByIdMap.continuous(key -> key.id, values(),
                ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, SpawnType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

        private final int id;
        private final String name;

        SpawnType(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public static DataResult<SpawnType> byName(String pTranslationKey) {
            for (SpawnType type : values()) {
                if (type.name.equals(pTranslationKey)) {
                    return DataResult.success(type);
                }
            }
            return DataResult.error(() -> "unkown type");
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
