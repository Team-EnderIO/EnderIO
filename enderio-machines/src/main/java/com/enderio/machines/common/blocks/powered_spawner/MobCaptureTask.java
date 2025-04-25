package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;

public class MobCaptureTask extends PoweredSpawnerTask {

    public MobCaptureTask(PoweredSpawnerBlockEntity blockEntity) {
        super(PoweredSpawnerMode.CAPTURE, blockEntity);
    }

    public MobCaptureTask(PoweredSpawnerBlockEntity blockEntity, int energyCost, EntityType<? extends Entity> entityType, MobSpawnMode spawnMode) {
        super(PoweredSpawnerMode.CAPTURE, blockEntity, energyCost, entityType, spawnMode);
    }

    @Override
    public boolean isCompleted() {
        // Ensure we have an item to take from
        var inputStack = PoweredSpawnerBlockEntity.INPUT.getItemStack(blockEntity);
        if (inputStack.isEmpty() || !inputStack.is(EIOItems.EMPTY_SOUL_VIAL)) {
            return true;
        }

        return super.isCompleted();
    }

    @Override
    protected void onTaskCompleted() {
        // Ensure we have a vial to take
        var inputStack = PoweredSpawnerBlockEntity.INPUT.getItemStack(blockEntity);
        if (inputStack.isEmpty() || !inputStack.is(EIOItems.EMPTY_SOUL_VIAL)) {
            return;
        }

        // Do not overwrite slot contents
        if (!PoweredSpawnerBlockEntity.OUTPUT.getItemStack(blockEntity).isEmpty()) {
            setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OUTPUT_FULL);
            return;
        }

        ItemStack filledStack = new ItemStack(EIOItems.FILLED_SOUL_VIAL.get(), 1);

        var entityTypeKey = BuiltInRegistries.ENTITY_TYPE.getKey(entityType());

        switch (spawnMode()) {
        case NEW -> filledStack.set(EIODataComponents.STORED_ENTITY, StoredEntityData.of(entityTypeKey));
        case COPY -> filledStack.set(EIODataComponents.STORED_ENTITY, blockEntity.getEntityData());
        }

        PoweredSpawnerBlockEntity.OUTPUT.setStackInSlot(blockEntity, filledStack);
        PoweredSpawnerBlockEntity.INPUT.setStackInSlot(blockEntity, inputStack.copyWithCount(inputStack.getCount() - 1));

        isComplete = true;
    }
}
