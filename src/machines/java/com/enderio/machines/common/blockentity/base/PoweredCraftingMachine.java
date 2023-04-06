package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.ICapacitorScalable;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.recipe.MachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Generic class for a machine that performs crafting recipes.
 */
public abstract class PoweredCraftingMachine<R extends MachineRecipe<C>, C extends Container> extends PoweredTaskMachineEntity<PoweredCraftingTask<R, C>> {


    /**
     * The recipe type this machine can accept.
     */
    protected final RecipeType<R> recipeType;

    public PoweredCraftingMachine(RecipeType<R> recipeType, ICapacitorScalable capacity, ICapacitorScalable transferRate, ICapacitorScalable usageRate,
        BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(capacity, transferRate, usageRate, type, worldPosition, blockState);
        this.recipeType = recipeType;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        // If the inventory changed, a new recipe may be ready.
        newTaskAvailable();
        super.onInventoryContentsChanged(slot);
    }

    /**
     * @apiNote If you override this, make sure to set the inventoryDirty flag to false!
     */

    @Nullable
    @Override
    protected PoweredCraftingTask<R, C> getNewTask() {
        return findRecipe()
            .map(this::createTask)
            .orElse(null);
    }

    @Nullable
    @Override
    protected PoweredCraftingTask<R, C> loadTask(CompoundTag nbt) {
        PoweredCraftingTask<R, C> task = createTask(null);
        task.deserializeNBT(nbt);
        return task;
    }

    /**
     * Find a recipe of this machine's type.
     */
    protected Optional<R> findRecipe() {
        return level
            .getRecipeManager()
            .getRecipeFor(recipeType, getContainer(), level);
    }

    /**
     * Create a new crafting task.
     * @param recipe The recipe to craft (or null).
     */
    protected abstract PoweredCraftingTask<R, C> createTask(@Nullable R recipe);

    /**
     * Get the container used for crafting.
     */
    protected abstract C getContainer();

    public RecipeType<R> getRecipeType() {
        return recipeType;
    }
}
