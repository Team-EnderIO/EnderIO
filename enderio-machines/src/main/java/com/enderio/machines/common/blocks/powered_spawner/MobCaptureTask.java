package com.enderio.machines.common.blocks.powered_spawner;

import com.enderio.base.api.soul.Soul;
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
        var inputSoulHandler = inputStack.getCapability(EIOCapabilities.SoulHandler.ITEM);
        if (inputStack.isEmpty() || inputSoulHandler == null || !inputSoulHandler.tryInsertSoul(getSoulForCapture(), true)) {
            return true;
        }

        return super.isCompleted();
    }

    @Override
    protected void onTaskCompleted() {
        final Soul capturedSoul = getSoulForCapture();

        // Ensure we have a storage to fill
        var inputStack = PoweredSpawnerBlockEntity.INPUT.getItemStack(blockEntity);
        if (inputStack.isEmpty()) {
            // Nothing to put into the output, so give up.
            isComplete = true;
            return;
        }

        // Clone the input
        var resultStack = inputStack.copyWithCount(1);
        var resultSoulHandler = resultStack.getCapability(EIOCapabilities.SoulHandler.ITEM);
        if (resultSoulHandler == null || !resultSoulHandler.tryInsertSoul(capturedSoul, true)) {
            // Cannot insert soul into the input, so give up
            isComplete = true;
            return;
        }

        // Insert the soul.
        if (!resultSoulHandler.tryInsertSoul(capturedSoul, false)) {
            // Unknown failure, give up.
            isComplete = true;
            return;
        }

        // If we can add another, leave it in the input for the next task.
        if (resultSoulHandler.tryInsertSoul(capturedSoul, true)) {
            PoweredSpawnerBlockEntity.INPUT.setStackInSlot(blockEntity, resultStack);
            isComplete = true;
            return;
        }

        // Otherwise, try and put it into the output.
        var currentOutputStack = PoweredSpawnerBlockEntity.OUTPUT.getItemStack(blockEntity);
        if (!currentOutputStack.isEmpty() && !ItemStack.isSameItemSameComponents(currentOutputStack, resultStack)) {
            setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OUTPUT_FULL);
            return;
        }

        if (currentOutputStack.isEmpty()) {
            PoweredSpawnerBlockEntity.OUTPUT.setStackInSlot(blockEntity, resultStack);
        } else {
            resultStack.setCount(currentOutputStack.getCount() + 1);
            PoweredSpawnerBlockEntity.OUTPUT.setStackInSlot(blockEntity, resultStack);
        }

        // Deduct input
        PoweredSpawnerBlockEntity.INPUT.setStackInSlot(blockEntity,
            inputStack.copyWithCount(inputStack.getCount() - 1));

        isComplete = true;
    }

    private Soul getSoulForCapture() {
        return switch (spawnMode()) {
        case NEW -> blockEntity.getBoundSoul().copyOnlyType();
        case COPY -> blockEntity.getBoundSoul().copy();
        };
    }
}
