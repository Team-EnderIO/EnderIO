package com.enderio.enderio.foundation.task.host;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.enderio.api.UseOnly;
import com.enderio.enderio.foundation.task.MachineTask;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.ValueIOSerializable;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

public abstract class MachineTaskHost implements ValueIOSerializable {
    @Nullable
    private MachineTask currentTask;

    private boolean isNewTaskAvailable;

    /**
     * A serialized task waiting for the level to load.
     */
    @Nullable
    private ValueInput pendingTask;
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
    protected abstract MachineTask loadTask(ValueInput input);

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
        if (level.isClientSide()) {
            return clientTaskProgress;
        }

        if (!hasTask()) {
            return 0;
        }

        return currentTask.getProgress();
    }

    public void tick() {
        findNewTaskIfAble();
        if (currentTask == null) {
            return;
        }

        if (!currentTask.isCompleted()) {
            currentTask.tick();
        }

        if (currentTask.isCompleted()) {
            currentTask = null;
            newTaskAvailable();
            findNewTaskIfAble();
        }
    }

    private void findNewTaskIfAble() {
        if (isNewTaskAvailable && canAcceptNewTask.get() && shouldStartNewTask()) {
            currentTask = getNewTask();
            isNewTaskAvailable = false;
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
            currentTask = loadTask(pendingTask);
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

    @Override
    public void serialize(ValueOutput output) {
        if (hasTask()) {
            output.putChild(KEY_TASK, getCurrentTask());
        }
    }

    @Override
    public void deserialize(ValueInput input) {
        hasLoaded = true;

        var task = input.child(KEY_TASK);

        // TODO: 1.21.8: Redesign so we don't need to know the level at load time.
        if (levelSupplier.get() == null) {
            // TODO: 1.21.8: Can we store a ValueInput here? is there a way to take a copy?
            task.ifPresent(t -> pendingTask = t);
        } else {
            task.ifPresentOrElse(t -> currentTask = loadTask(t),
                () -> currentTask = getNewTask());
        }
    }

    // endregion
}
