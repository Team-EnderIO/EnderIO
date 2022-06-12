package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.machines.recipes.MachineRecipe;
import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.SlicerBlockEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public abstract class PoweredCraftingMachine<R extends MachineRecipe<C>, C extends Container> extends PoweredTaskMachineEntity<PoweredCraftingTask> {
    // Flag for determining if we should re-check for a possible recipe.
    protected boolean inventoryDirty = true;

    protected final RecipeType<R> recipeType;

    public PoweredCraftingMachine(RecipeType<R> recipeType, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey, BlockEntityType<?> pType,
        BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);
        this.recipeType = recipeType;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        inventoryDirty = true;
        super.onInventoryContentsChanged(slot);
    }

    @Override
    protected boolean hasNextTask() {
        return inventoryDirty;
    }

    /**
     * @apiNote If you override this, make sure to set the inventoryDirty flag to false!
     */
    @Override
    protected @Nullable PoweredCraftingTask<R, C> getNextTask() {
        inventoryDirty = false;
        return findRecipe()
            .map(this::createTask)
            .orElse(null);
    }

    protected Optional<R> findRecipe() {
        return level
            .getRecipeManager()
            .getRecipeFor(recipeType, getContainer(), level);
    }

    @Override
    protected @Nullable PoweredCraftingTask<R, C> loadTask(CompoundTag nbt) {
        PoweredCraftingTask<R, C> task = createTask(null);
        task.deserializeNBT(nbt);
        return task;
    }

    protected abstract PoweredCraftingTask<R, C> createTask(@Nullable R recipe);

    protected abstract C getContainer();
}
