package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.base.api.attachment.Soul;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class MobCaptureTask extends PoweredSpawnerTask {

    public MobCaptureTask(PoweredSpawnerBlockEntity blockEntity) {
        super(PoweredSpawnerMode.CAPTURE, blockEntity);
    }

    public MobCaptureTask(PoweredSpawnerBlockEntity blockEntity, int energyCost,
            EntityType<? extends Entity> entityType, MobSpawnMode spawnMode) {
        super(PoweredSpawnerMode.CAPTURE, blockEntity, energyCost, entityType, spawnMode);
    }

    @Override
    public boolean isCompleted() {
        // Ensure we have an item to take from
        var inputStack = PoweredSpawnerBlockEntity.INPUT.getItemStack(blockEntity);
        var inputSoulStorage = inputStack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        if (inputStack.isEmpty() || inputSoulStorage == null || inputSoulStorage.hasSoul()) {
            return true;
        }

        return super.isCompleted();
    }

    @Override
    protected void onTaskCompleted() {
        // Ensure we have a vial to take
        var inputStack = PoweredSpawnerBlockEntity.INPUT.getItemStack(blockEntity);
        var inputSoulStorage = inputStack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        if (inputStack.isEmpty() || inputSoulStorage == null || inputSoulStorage.hasSoul()) {
            return;
        }

        // Do not overwrite slot contents
        if (!PoweredSpawnerBlockEntity.OUTPUT.getItemStack(blockEntity).isEmpty()) {
            setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OUTPUT_FULL);
            return;
        }

        ItemStack filledStack = inputStack.copyWithCount(1);
        var filledSoulStorage = filledStack.getCapability(EIOCapabilities.SingleSoulStorage.ITEM);
        if (filledSoulStorage == null) {
            // TODO: Log warning?
            isComplete = true;
            return;
        }

        var entityTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityType());

        switch (spawnMode()) {
        case NEW -> filledSoulStorage.setSoul(Soul.of(entityTypeKey));
        case COPY -> filledSoulStorage.setSoul(blockEntity.getBoundSoul().copy());
        }

        PoweredSpawnerBlockEntity.OUTPUT.setStackInSlot(blockEntity, filledStack);
        PoweredSpawnerBlockEntity.INPUT.setStackInSlot(blockEntity,
                inputStack.copyWithCount(inputStack.getCount() - 1));

        isComplete = true;
    }
}
