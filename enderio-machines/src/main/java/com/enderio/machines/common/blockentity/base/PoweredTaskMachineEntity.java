package com.enderio.machines.common.blockentity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A machine that consumes power to execute a task
 */
public abstract class PoweredTaskMachineEntity extends PoweredMachineEntity {
    // Controls whether the task action is executed each tick and consumes energy at a flat rate
    public enum TaskType {
        /**
         * A task that is run continuously, rather than accumulating progress.
         */
        CONTINUOUS,

        /**
         * A task that is repeated, accumulates progress.
         */
        REPEATING
    }

    // TODO: We need to store the following in the implementor: Current Recipe, Energy Consumed and the Energy Total

    private int energyConsumed;
    private String currentTask;

    public PoweredTaskMachineEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);

        // TODO: Dataslot for syncing task progress
    }

    protected abstract TaskType getTaskType();

    // Get the amount of energy required for this task. The machine will consume energy at the rate of the capacitor until this is reached.
    // If the task is continuous however, this is energy per tick.
    protected abstract int getEnergyRequired();

    // Once the required energy is consumed, this is called to complete the task (i.e. craft the item)
    protected abstract void executeTask();

    // Get the current task ID. Will be saved.
    protected abstract String getCurrentTaskID();

    @Override
    public void tick() {
        if (shouldTick()) {
            // TODO: Perform actions. The flow is: Ensure the current task is still the same, check if we've consumed all the energy, if not consume more energy, if we've consumed enough execute the task and setup next task.

        }
        super.tick();
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
//        pTag.putInt("Progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }
}
