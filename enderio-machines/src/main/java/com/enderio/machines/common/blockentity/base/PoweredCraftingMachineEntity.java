package com.enderio.machines.common.blockentity.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class PoweredCraftingMachineEntity extends PoweredTaskMachineEntity {
    private Recipe<?> recipe;

    public PoweredCraftingMachineEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    protected abstract RecipeType<?> getRecipeType();

    @Override
    protected TaskType getTaskType() {
        return TaskType.REPEATING;
    }

    @Override
    protected int getEnergyRequired() {
        // TODO: Get it from the recipe or use a energy consumption helper.
        return 64;
    }

    @Override
    protected void executeTask() {
        // TODO: Execute the recipe.
//        recipe.assemble()
    }

    @Override
    protected String getCurrentTaskID() {
        if (recipe != null) {
            return recipe.getId().toString();
        }
        return "";
    }

    private Recipe<?> getRecipe() {
//        return pLevel.getRecipeManager().getRecipeFor(getRecipeType(), container, level);
        return null;
    }
}
