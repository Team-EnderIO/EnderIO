package com.enderio.machines.common.blockentity.task.host;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.machines.common.blockentity.task.CraftingMachineTask;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class CraftingMachineTaskHost<R extends MachineRecipe<C>, C extends Container> extends MachineTaskHost {

    public interface ICraftingMachineTaskFactory<T extends CraftingMachineTask<R, C>, R extends MachineRecipe<C>, C extends Container> {
        T createTask(Level level, C container, @Nullable R recipe);
    }

    private final RecipeType<R> recipeType;
    private final C container;
    private final ICraftingMachineTaskFactory<? extends CraftingMachineTask<R, C>, R, C> taskFactory;

    /**
     * This should be constructed in the constructor of your block entity.
     */
    public CraftingMachineTaskHost(EnderBlockEntity blockEntity, Supplier<Boolean> canAcceptNewTask, RecipeType<R> recipeType,
        C container, ICraftingMachineTaskFactory<? extends CraftingMachineTask<R, C>, R, C> taskFactory) {
        super(blockEntity, canAcceptNewTask);
        this.recipeType = recipeType;
        this.container = container;
        this.taskFactory = taskFactory;
    }

    public final C getContainer() {
        return container;
    }

    @Nullable
    public CraftingMachineTask<R, C> getCurrentTask() {
        //noinspection unchecked
        return (CraftingMachineTask<R, C>)super.getCurrentTask();
    }

    // region MachineTaskHost Implementation

    @Override
    protected @Nullable CraftingMachineTask<R, C> getNewTask() {
        if (getLevel() == null) {
            return null;
        }

        return findRecipe().map(r -> taskFactory.createTask(getLevel(), container, r)).orElse(null);
    }

    @Override
    protected @Nullable CraftingMachineTask<R, C> loadTask(CompoundTag nbt) {
        if (getLevel() == null) {
            return null;
        }

        CraftingMachineTask<R, C> task = taskFactory.createTask(getLevel(), container, null);
        task.deserializeNBT(nbt);
        return task;
    }

    @Override
    protected boolean shouldStartNewTask() {
        if (super.shouldStartNewTask()) {
            return true;
        }

        // If recipe has changed
        var currentRecipe = findRecipe();
        return currentRecipe.map(r -> !r.equals(getCurrentTask().getRecipe())).orElse(true);
    }

    // endregion

    protected Optional<R> findRecipe() {
        Level level = getLevel();
        if (level == null) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(recipeType, container, level);
    }
}
