package com.enderio.enderio.content.machines.powered_spawner;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.soul.Soul;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

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

        try (Transaction rootTransaction = Transaction.openRoot()) {
            var inputResource = inventory.getResource(PoweredSpawnerBlockEntity.INPUT);
            if (inputResource.isEmpty()) {
                isComplete = true;
                return;
            }

            // Try and take one input item
            int extracted = inventory.extract(PoweredSpawnerBlockEntity.INPUT, inputResource, 1, rootTransaction);
            if (extracted != 1) {
                isComplete = true;
                return;
            }

            // Convert to stack
            var resultStack = inputResource.toStack(extracted);

            // Insert the soul into the stack
            var resultSoulHandler = resultStack.getCapability(EnderIOCapabilities.SOUL_HANDLER_ITEM);
            if (resultSoulHandler == null || !resultSoulHandler.tryInsertSoul(capturedSoul, false)) {
                // Cannot insert soul into the input, so give up
                isComplete = true;
                return;
            }

            // Now we'll see if the stack cannot contain more of this soul
            boolean canInsertMore = resultSoulHandler.tryInsertSoul(capturedSoul, true);

            // Now, if we can insert more attempt to place back in the INPUT
            int amountToInsert = resultStack.getCount();
            if (canInsertMore) {
                int inserted = inventory.insert(PoweredSpawnerBlockEntity.INPUT, ItemResource.of(resultStack), amountToInsert, rootTransaction);
                if (inserted == amountToInsert) {
                    rootTransaction.commit();
                    isComplete = true;
                    return;
                }

                amountToInsert -= inserted;
            }

            // If we haven't inserted everything, try and drop it into OUTPUT
            int inserted = inventory.insert(PoweredSpawnerBlockEntity.OUTPUT, ItemResource.of(resultStack), amountToInsert, rootTransaction);
            if (inserted == amountToInsert) {
                rootTransaction.commit();
                isComplete = true;
            } else {
                // We cannot output everything, so we're blocked on the output
                setBlockedReason(PoweredSpawnerBlockEntity.SpawnerBlockedReason.OUTPUT_FULL);
            }
        }
    }

    private Soul getSoulForCapture() {
        return switch (spawnMode()) {
        case NEW -> blockEntity.getBoundSoul().copyOnlyType();
        case COPY -> blockEntity.getBoundSoul().copy();
        };
    }
}
