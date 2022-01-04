package com.enderio.machines.common.blockentity.base;

import com.enderio.machines.common.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Optional;

public abstract class PoweredCraftingMachineEntity<R extends Recipe<Container>> extends PoweredMachineEntity {
    private int energyConsumed;
    private R currentRecipe;

    public PoweredCraftingMachineEntity(MachineTier tier, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(tier, pType, pWorldPosition, pBlockState);
    }

    protected abstract RecipeType<?> getRecipeType();

    protected abstract void deductIngredients(R recipe);

    protected abstract int getEnergyCost(R recipe);

    protected R getCurrentRecipe() {
        return currentRecipe;
    }

    @Override
    public void tick() {
        if (shouldAct()) {
            if (getCurrentRecipe() != null) {
                if (energyConsumed <= getEnergyCost(getCurrentRecipe()))
                    energyConsumed += 1;

                if (canTakeOutput(getCurrentRecipe()) && energyConsumed >= getEnergyCost(getCurrentRecipe())) {
                    getItemHandlerMaster().forceInsertItem(3, processResultStack(currentRecipe.assemble(new RecipeWrapper(getItemHandlerMaster()))));
                    clearRecipe();
                    selectNextRecipe();
                }
            } else {
                selectNextRecipe();
            }
        }
        super.tick();
    }

    // Useful for applying extra NBT or processing etc.
    // TODO: Will be used for smelting multiple at a time. This will be the hook for changing the count.
    protected ItemStack processResultStack(ItemStack stack) {
        return stack;
    }

    protected void selectNextRecipe() {
        getRecipe().ifPresent(this::setCurrentRecipe);
    }

    protected void setCurrentRecipe(R recipe) {
        if (canTakeOutput(recipe)) {
            currentRecipe = recipe;
            energyConsumed = 0;
            deductIngredients(recipe);
        }
    }

    protected void clearRecipe() {
        currentRecipe = null;
        energyConsumed = 0;
    }

    protected boolean canTakeOutput(R recipe) {
        return getItemHandlerMaster().canForceInsert(3, recipe.assemble(new RecipeWrapper(getItemHandlerMaster())));
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        pTag.putInt("EnergyConsumed", energyConsumed);
        if (currentRecipe != null) {
            pTag.putString("Recipe", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }

    private Optional<R> getRecipe() {
        return level.getRecipeManager().getRecipeFor((RecipeType<R>) getRecipeType(), new RecipeWrapper(getItemHandlerMaster()), level);
    }
}
