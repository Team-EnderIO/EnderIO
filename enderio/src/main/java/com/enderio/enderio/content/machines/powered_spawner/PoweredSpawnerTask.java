package com.enderio.enderio.content.machines.powered_spawner;

import com.enderio.enderio.foundation.energy.MachineEnergyHandler;
import com.enderio.enderio.foundation.task.PoweredMachineTask;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class PoweredSpawnerTask implements PoweredMachineTask {
    // Used to determine if task should cancel
    protected final PoweredSpawnerMode spawnerMode;

    protected final PoweredSpawnerBlockEntity blockEntity;

    private boolean isLoaded;
    private int energyCost;
    private int energyConsumed;

    private EntityType<? extends Entity> entityType;
    private MobSpawnMode spawnMode;

    private PoweredSpawnerBlockEntity.SpawnerBlockedReason blockedReason = PoweredSpawnerBlockEntity.SpawnerBlockedReason.NONE;
    protected boolean isComplete;

    public PoweredSpawnerTask(PoweredSpawnerMode spawnerMode, PoweredSpawnerBlockEntity blockEntity) {
        this.spawnerMode = spawnerMode;
        this.blockEntity = blockEntity;
    }

    public PoweredSpawnerTask(PoweredSpawnerMode spawnerMode, PoweredSpawnerBlockEntity blockEntity, int energyCost,
            EntityType<? extends Entity> entityType, MobSpawnMode spawnMode) {
        this.spawnerMode = spawnerMode;
        if (energyCost <= 0) {
            throw new IllegalArgumentException("Energy cost must be greater than 0");
        }

        this.isLoaded = true;
        this.blockEntity = blockEntity;
        this.energyCost = energyCost;
        this.entityType = entityType;
        this.spawnMode = spawnMode;
    }

    @Override
    public boolean isCompleted() {
        return blockEntity.getMode() != spawnerMode || isComplete;
    }

    public PoweredSpawnerBlockEntity.SpawnerBlockedReason getBlockedReason() {
        return blockedReason;
    }

    protected void setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason reason) {
        this.blockedReason = reason;
    }

    protected EntityType<? extends Entity> entityType() {
        return entityType;
    }

    protected MobSpawnMode spawnMode() {
        return spawnMode;
    }

    @Override
    public void tick() {
        if (!isLoaded) {
            // TODO: maybe just mark as complete?
            return;
        }

        if (energyConsumed < energyCost) {
            energyConsumed += getEnergyStorage().consume(energyCost - energyConsumed, null);
        } else {
            onTaskCompleted();
        }
    }

    protected abstract void onTaskCompleted();

    @Override
    public MachineEnergyHandler getEnergyStorage() {
        return blockEntity.getEnergyStorage();
    }

    @Override
    public float getProgress() {
        return energyConsumed / (float) energyCost;
    }

    // Serialization functions are final to prevent adding extra data in subclasses
    // by accident
    // Tread carefully :)

    @Override
    public final void serialize(ValueOutput output) {
        output.putInt("EnergyCost", energyCost);
        output.putInt("EnergyConsumed", energyConsumed);
        output.store("EntityType", BuiltInRegistries.ENTITY_TYPE.byNameCodec(), entityType);
        output.store("SpawnMode", MobSpawnMode.CODEC, spawnMode);
    }

    @Override
    public final void deserialize(ValueInput input) {
        isLoaded = true;

        energyCost = input.getIntOr("EnergyCost", 0);
        energyConsumed = input.getIntOr("EnergyConsumed", 0);

        // If we can't load the entity type, this task has to be marked as complete.
        input.read("EntityType", BuiltInRegistries.ENTITY_TYPE.byNameCodec())
            .ifPresentOrElse(entityType -> this.entityType = entityType,
                () -> isComplete = true);

        // If we can't load the spawn mode, mark as complete to avoid making a fault assumption.
        // Don't want to introduce any weird bugs where you can copy NBT on entities it is disabled for.
        input.read("SpawnMode", MobSpawnMode.CODEC)
            .ifPresentOrElse(spawnMode -> this.spawnMode = spawnMode,
                () -> isComplete = true);
    }
}
