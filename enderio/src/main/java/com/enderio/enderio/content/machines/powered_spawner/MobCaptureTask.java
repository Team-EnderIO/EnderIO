package com.enderio.enderio.content.machines.powered_spawner;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
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
        var inputStack = blockEntity.getInventory().getStack(PoweredSpawnerBlockEntity.INPUT);
        var inputSoulHandler = inputStack.getCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM);
        if (inputStack.isEmpty() || inputSoulHandler == null || !inputSoulHandler.tryInsertSoul(getSoulForCapture(), true)) {
            return true;
        }

        return super.isCompleted();
    }

    @Override
    protected void onTaskCompleted() {
        final Soul capturedSoul = getSoulForCapture();
        var inventory = blockEntity.getInventory();

        // Ensure we have a storage to fill
        var inputStack = inventory.getStack(PoweredSpawnerBlockEntity.INPUT);
        if (inputStack.isEmpty()) {
            // Nothing to put into the output, so give up.
            isComplete = true;
            return;
        }

        // Clone the input
        var resultStack = inputStack.copyWithCount(1);
        var resultSoulHandler = resultStack.getCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM);
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
            inventory.setStack(PoweredSpawnerBlockEntity.INPUT, resultStack);
            isComplete = true;
            return;
        }

        // Otherwise, try and put it into the output.
        var currentOutputStack = inventory.getStack(PoweredSpawnerBlockEntity.OUTPUT);
        if (!currentOutputStack.isEmpty() && !ItemStack.isSameItemSameComponents(currentOutputStack, resultStack)) {
            setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OUTPUT_FULL);
            return;
        }

        if (currentOutputStack.isEmpty()) {
            inventory.setStack(PoweredSpawnerBlockEntity.OUTPUT, resultStack);
        } else {
            resultStack.setCount(currentOutputStack.getCount() + 1);
            inventory.setStack(PoweredSpawnerBlockEntity.OUTPUT, resultStack);
        }

        // Deduct input
        inventory.setStack(PoweredSpawnerBlockEntity.INPUT,
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
