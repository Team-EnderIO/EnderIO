package com.enderio.machines.common.blocks.base.task.host;

import com.enderio.base.api.UseOnly;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.blocks.base.task.MachineTask;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

public abstract class MachineTaskHost {
    @Nullable
    private MachineTask currentTask;

    private boolean isNewTaskAvailable;

    /**
     * A serialized task waiting for the level to load.
     */
    @Nullable
    private CompoundTag pendingTask;
    private boolean hasLoaded;

    @UseOnly(LogicalSide.CLIENT)
    private float clientTaskProgress;

    private final Supplier<Level> levelSupplier;
    private final Supplier<Boolean> canAcceptNewTask;

    /**
     * This should be constructed in the constructor of your block entity.
     */
    public MachineTaskHost(EnderBlockEntity blockEntity, Supplier<Boolean> canAcceptNewTask) {
        levelSupplier = blockEntity::getLevel;
        this.canAcceptNewTask = canAcceptNewTask;

        // Add sync data slot for crafting progress
        blockEntity.addDataSlot(NetworkDataSlot.FLOAT.create(this::getProgress, p -> clientTaskProgress = p));
    }

    @Nullable
    protected Level getLevel() {
        return levelSupplier.get();
    }

    // region Abstract Implementation

    /**
     * Get the new task.
     */
    @Nullable
    protected abstract MachineTask getNewTask();

    /**
     * Load the task from NBT.
     */
    @Nullable
    protected abstract MachineTask loadTask(HolderLookup.Provider lookupProvider, CompoundTag nbt);

    // endregion

    // region Task Handling

    @Nullable
    public MachineTask getCurrentTask() {
        return currentTask;
    }

    public final boolean hasTask() {
        return currentTask != null && !currentTask.isCompleted();
    }

    protected boolean shouldStartNewTask() {
        return (currentTask == null || currentTask.isCompleted());
    }

    public final float getProgress() {
        Level level = levelSupplier.get();
        if (level == null) {
            return 0;
        }

        // Client has no knowledge of task, so we use a synced field.
        if (level.isClientSide) {
            return clientTaskProgress;
        }

        if (!hasTask()) {
            return 0;
        }

        return currentTask.getProgress();
    }

    public void tick() {
        // If we have no active task, get a new one
        if (isNewTaskAvailable && canAcceptNewTask.get() && shouldStartNewTask()) {
            currentTask = getNewTask();
            isNewTaskAvailable = false;
        }

        // If we have an unfinished task, continue it.
        if (currentTask != null && !currentTask.isCompleted()) {
            currentTask.tick();
        }

        // If the task finished, next tick we'll try find a new one.
        // Just in case the task didn't perform an action that caused a task change.
        if (currentTask != null && currentTask.isCompleted()) {
            newTaskAvailable();
        }
    }

    public final void newTaskAvailable() {
        isNewTaskAvailable = true;
    }

    public final void onLevelReady() {
        // If load() hasn't been called yet, don't
        if (!hasLoaded) {
            return;
        }

        // Load any pending tasks.
        if (pendingTask != null) {
            currentTask = loadTask(getLevel().registryAccess(), pendingTask);
            pendingTask = null;
        }

        // If we have no task, check for an initial one
        if (currentTask == null) {
            currentTask = getNewTask();
        }
    }

    // endregion

    // region Serialization

    private static final String KEY_TASK = "Task";

    public void save(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        if (hasTask()) {
            tag.put(KEY_TASK, getCurrentTask().serializeNBT(lookupProvider));
        }
    }

    public void load(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        hasLoaded = true;

        if (levelSupplier.get() == null) {
            if (tag.contains(KEY_TASK)) {
                pendingTask = tag.getCompound(KEY_TASK).copy();
            }
        } else {
            if (tag.contains(KEY_TASK)) {
                currentTask = loadTask(lookupProvider, tag.getCompound(KEY_TASK));
            } else {
                currentTask = getNewTask();
            }
        }
    }

    // endregion
}
