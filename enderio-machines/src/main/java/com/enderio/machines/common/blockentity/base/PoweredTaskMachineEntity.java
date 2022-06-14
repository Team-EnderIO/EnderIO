package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.common.blockentity.sync.FloatDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.blockentity.task.PoweredTask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
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
            if ((currentTask == null || currentTask.isComplete()) && hasNextTask()) {
                currentTask = getNextTask();
            }

            // If we have an unfinished task, continue it.
            if (currentTask != null && !currentTask.isComplete()) {
                currentTask.tick();
            }

            // Update block state
            if (getBlockState().getValue(ProgressMachineBlock.POWERED) != hasTask()) {
                level.setBlock(getBlockPos(), getBlockState().setValue(ProgressMachineBlock.POWERED, hasTask()), Block.UPDATE_ALL);
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

    protected boolean hasTask() {
        return currentTask != null;
    }

    protected boolean hasNextTask() {
        return true;
    }

    protected abstract @Nullable T getNextTask();

    protected abstract @Nullable T loadTask(CompoundTag nbt);

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (currentTask != null)
            pTag.put("task", currentTask.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        if (pTag.contains("task"))
            currentTask = loadTask(pTag.getCompound("task"));
    }
}
