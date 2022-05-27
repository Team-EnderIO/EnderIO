package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.common.blockentity.sync.FloatDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.blockentity.task.PoweredTask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class PoweredTaskMachineEntity<T extends PoweredTask> extends PowerConsumingMachineEntity {
    private T currentTask;

    private float clientProgress;

    public PoweredTaskMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey,
        BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);

        // Sync machine progress to the client.
        addDataSlot(new FloatDataSlot(this::getProgress, p -> clientProgress = p, SyncMode.GUI));
    }

    @Override
    public void serverTick() {
        if (canAct()) {
            // If we have no active task, get a new one
            if (currentTask == null || currentTask.isComplete()) {
                currentTask = getNextTask();
            }

            // If we have an unfinished task, continue it.
            if (currentTask != null && !currentTask.isComplete()) {
                currentTask.tick();
            }
        }

        super.serverTick();
    }

    public float getProgress() {
        if (isClientSide())
            return clientProgress;
        if (currentTask == null)
            return 0;
        return currentTask.getProgress();
    }

    protected T getCurrentTask() {
        return currentTask;
    }

    protected abstract @Nullable T getNextTask();

//    protected abstract @Nullable T loadTask(CompoundTag tag);

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (currentTask != null)
            pTag.put("task", currentTask.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        // TODO: Work out how we're going to deal with the level not being present yet? Do we just store the nbt tag until we're loaded?
//        currentTask = loadTask(pTag.getCompound("task"));
    }
}
