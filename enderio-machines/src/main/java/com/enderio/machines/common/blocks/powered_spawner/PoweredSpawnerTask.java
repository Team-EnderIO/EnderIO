package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.machines.common.blocks.base.task.PoweredMachineTask;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import java.util.Objects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

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
            energyConsumed += getEnergyStorage().consumeEnergy(energyCost - energyConsumed, false);
        } else {
            onTaskCompleted();
        }
    }

    protected abstract void onTaskCompleted();

    @Override
    public IMachineEnergyStorage getEnergyStorage() {
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
    public final CompoundTag serializeNBT(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        tag.putInt("EnergyCost", energyCost);
        tag.putInt("EnergyConsumed", energyConsumed);

        var entityTypeId = BuiltInRegistries.ENTITY_TYPE.getKey(entityType);
        tag.putString("EntityType", entityTypeId.toString());

        tag.put("SpawnMode", spawnMode.save(provider));

        return tag;
    }

    @Override
    public final void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        isLoaded = true;

        energyCost = compoundTag.getInt("EnergyCost");
        energyConsumed = compoundTag.getInt("EnergyConsumed");

        var entityTypeId = ResourceLocation.parse(compoundTag.getString("EntityType"));
        var optEntityType = BuiltInRegistries.ENTITY_TYPE.getOptional(entityTypeId);

        if (optEntityType.isEmpty()) {
            // Cannot proceed, mark as complete.
            isComplete = true;
            return;
        }

        entityType = optEntityType.get();

        // TODO: Non crashing way to make sure this is right.
        spawnMode = MobSpawnMode.parse(provider, Objects.requireNonNull(compoundTag.get("SpawnMode")));
    }
}
