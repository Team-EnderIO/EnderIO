package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.ICapacitorScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.machines.common.blockentity.task.PoweredTask;
import com.enderio.machines.common.blockentity.task.host.MachineTaskHost;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Generic class for a machine that can perform a task.
 */
public abstract class PoweredTaskMachineEntity<T extends PoweredTask> extends PoweredMachineEntity {

    private final MachineTaskHost machineTaskHost;

    public PoweredTaskMachineEntity(ICapacitorScalable capacity, ICapacitorScalable usageRate,
        BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, capacity, usageRate, type, worldPosition, blockState);

        // TODO: energyStorage.isEmpty() method.
        machineTaskHost = new MachineTaskHost(this, () -> energyStorage.getEnergyStored() > 0) {
            @Override
            protected @Nullable T getNewTask() {
                return PoweredTaskMachineEntity.this.getNewTask();
            }

            @Override
            protected @Nullable T loadTask(CompoundTag nbt) {
                return PoweredTaskMachineEntity.this.loadTask(nbt);
            }
        };
    }

    @Override
    public void onLoad() {
        super.onLoad();
        machineTaskHost.onLevelReady();
    }

    @Override
    public void serverTick() {
        if (canAct()) {
            machineTaskHost.tick();
        }

        // Update block state
        boolean active = isActive();
        if (getBlockState().getValue(ProgressMachineBlock.POWERED) != active) {
            level.setBlock(getBlockPos(), getBlockState().setValue(ProgressMachineBlock.POWERED, active), Block.UPDATE_ALL);
        }

        super.serverTick();
    }

    /**
     * Get task completion progress
     * @return Percentage completion, represented 0.0->1.0
     */
    public float getProgress() {
        return machineTaskHost.getProgress();
    }

    /**
     * Call this to indicate a new task has become available.
     */
    public void newTaskAvailable() {
        machineTaskHost.newTaskAvailable();
    }

    /**
     * Get the current task
     */
    @Nullable
    protected T getCurrentTask() {
        //noinspection unchecked
        return (T) machineTaskHost.getCurrentTask();
    }

    /**
     * Whether the machine is executing a task.
     */
    protected boolean hasTask() {
        return machineTaskHost.hasTask();
    }

    /**
     * Whether the machine is currently active.
     */
    protected boolean isActive() {
        return canAct() && hasTask() && energyStorage.getEnergyStored() > 0;
    }

    /**
     * Get the new task.
     */
    @Nullable
    protected abstract T getNewTask();

    /**
     * Load the task from NBT.
     */
    @Nullable
    protected abstract T loadTask(CompoundTag nbt);

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        machineTaskHost.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        machineTaskHost.load(pTag);
    }
}
