package com.enderio.machines.common.blockentity.base;

import com.enderio.api.UseOnly;
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
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

/**
 * Generic class for a machine that can perform a task.
 */
public abstract class PoweredTaskMachineEntity<T extends PoweredTask> extends PowerConsumingMachineEntity {
    /**
     * The current task being executed.
     */
    private @Nullable T currentTask;

    /**
     * A serialized task waiting for the level to load.
     */
    private @Nullable CompoundTag pendingTask;

    /**
     * Whether a new task is available.
     */
    private boolean hasNewTask;

    /**
     * The task progress (client side)
     */
    @UseOnly(LogicalSide.CLIENT)
    private float clientProgress;

    public PoweredTaskMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey,
        BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(capacityKey, transferKey, energyUseKey, type, worldPosition, blockState);

        // Sync machine progress to the client.
        addDataSlot(new FloatDataSlot(this::getProgress, p -> clientProgress = p, SyncMode.GUI));
    }

    @Override
    public void onLoad() {
        super.onLoad();

        // Load any pending tasks.
        if (pendingTask != null) {
            currentTask = loadTask(pendingTask);
            pendingTask = null;
        }

        // If we have no task, check for an initial one
        if (currentTask == null) {
            currentTask = getNewTask();
        }
    }

    @Override
    public void serverTick() {
        if (canAct()) {
            // If we have no active task, get a new one
            if ((currentTask == null || currentTask.isComplete()) && hasNewTask && energyStorage.getEnergyStored() > 0) {
                currentTask = getNewTask();
                hasNewTask = false;
            }

            // If we have an unfinished task, continue it.
            if (currentTask != null && !currentTask.isComplete()) {
                currentTask.tick();
            }

            // If the task finished, next tick we'll try find a new one.
            // Just in case the task didn't perform an action that caused a task change.
            if (currentTask != null && currentTask.isComplete()) {
                newTaskAvailable();
            }

            // Update block state
            if (getBlockState().getValue(ProgressMachineBlock.POWERED) != hasTask()) {
                level.setBlock(getBlockPos(), getBlockState().setValue(ProgressMachineBlock.POWERED, hasTask()), Block.UPDATE_ALL);
            }
        }

        super.serverTick();
    }

    /**
     * Get task completion progress
     * @return Percentage completion, represented 0.0->1.0
     */
    public float getProgress() {
        if (isClientSide())
            return clientProgress;
        if (currentTask == null)
            return 0;
        return currentTask.getProgress();
    }

    /**
     * Call this to indicate a new task has become available.
     */
    public void newTaskAvailable() {
        hasNewTask = true;
    }

    /**
     * Get the current task
     */
    protected @Nullable T getCurrentTask() {
        return currentTask;
    }

    /**
     * Whether the machine is executing a task.
     */
    protected boolean hasTask() {
        return currentTask != null;
    }

    /**
     * Get the new task.
     */
    protected abstract @Nullable T getNewTask();

    /**
     * Load the task from NBT.
     */
    protected abstract @Nullable T loadTask(CompoundTag nbt);

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        if (currentTask != null)
            pTag.put("Task", currentTask.serializeNBT());
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);

        // Store the task for the onLoad() call
        if (pTag.contains("Task"))
            pendingTask = pTag.getCompound("Task").copy();
    }
}
