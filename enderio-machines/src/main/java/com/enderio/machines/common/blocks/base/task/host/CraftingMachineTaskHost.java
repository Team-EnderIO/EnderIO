package com.enderio.machines.common.blocks.base.task.host;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.blocks.base.task.CraftingMachineTask;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CraftingMachineTaskHost<R extends MachineRecipe<T>, T extends RecipeInput> extends MachineTaskHost {

    public interface CraftingMachineTaskFactory<T extends CraftingMachineTask<R, C>, R extends MachineRecipe<C>, C extends RecipeInput> {
        T createTask(Level level, C container, @Nullable RecipeHolder<R> recipe);
    }

    private final RecipeType<R> recipeType;
    private final CraftingMachineTaskFactory<? extends CraftingMachineTask<R, T>, R, T> taskFactory;
    private final Supplier<T> recipeInputSupplier;

    /**
     * This should be constructed in the constructor of your block entity.
     */
    public CraftingMachineTaskHost(EnderBlockEntity blockEntity, Supplier<Boolean> canAcceptNewTask,
            RecipeType<R> recipeType, CraftingMachineTaskFactory<? extends CraftingMachineTask<R, T>, R, T> taskFactory,
            Supplier<T> recipeInputSupplier) {
        super(blockEntity, canAcceptNewTask);
        this.recipeType = recipeType;
        this.taskFactory = taskFactory;
        this.recipeInputSupplier = recipeInputSupplier;
    }

    @Nullable
    public CraftingMachineTask<R, T> getCurrentTask() {
        // noinspection unchecked
        return (CraftingMachineTask<R, T>) super.getCurrentTask();
    }

    // region MachineTaskHost Implementation

    @Override
    protected @Nullable CraftingMachineTask<R, T> getNewTask() {
        if (getLevel() == null) {
            return null;
        }

        return findRecipe().map(r -> taskFactory.createTask(getLevel(), recipeInputSupplier.get(), r)).orElse(null);
    }

    @Override
    protected @Nullable CraftingMachineTask<R, T> loadTask(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
        if (getLevel() == null) {
            return null;
        }

        CraftingMachineTask<R, T> task = taskFactory.createTask(getLevel(), recipeInputSupplier.get(), null);
        task.deserializeNBT(lookupProvider, nbt);
        return task;
    }

    @Override
    protected boolean shouldStartNewTask() {
        if (super.shouldStartNewTask()) {
            return true;
        }

        // If recipe has changed
        var currentRecipe = findRecipe();
        return currentRecipe.map(r -> !r.value().equals(getCurrentTask().getRecipe())).orElse(true);
    }

    // endregion

    protected T createRecipeInput() {
        return recipeInputSupplier.get();
    }

    protected Optional<RecipeHolder<R>> findRecipe() {
        Level level = getLevel();
        if (level == null) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(recipeType, recipeInputSupplier.get(), level);
    }
}
