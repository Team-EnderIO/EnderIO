package com.enderio.machines.common.blockentity.base;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.block.ProgressMachineBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public abstract class PoweredCraftingMachineEntity<R extends Recipe<Container>> extends PoweredMachineEntity {
    private int energyConsumed;
    private R currentRecipe;

    public PoweredCraftingMachineEntity(MachineTier tier, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(tier, pType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        boolean active = false;

        if (shouldAct()) {
            // TODO: Check we have energy.
            if (canCraft()) {
                if (energyConsumed <= getEnergyCost(getCurrentRecipe())) {
                    energyConsumed += 1;
                    active = true;
                }

                if (canTakeOutput(getCurrentRecipe()) && energyConsumed >= getEnergyCost(getCurrentRecipe())) {
                    assembleRecipe(getCurrentRecipe());
                    clearRecipe();
                    selectNextRecipe();
                }
            } else if (canSelectRecipe()) {
                selectNextRecipe();
            }
        }

        // We do this outside of shouldAct() so it still fires if we have no redstone signal
        if (isServer()) {
            if (getBlockState().getValue(ProgressMachineBlock.POWERED) != active) {
                level.setBlock(getBlockPos(), getBlockState().setValue(ProgressMachineBlock.POWERED, active), Block.UPDATE_ALL);
            }
        }

        super.tick();
    }

    protected void setCurrentRecipe(R recipe) {
        // TODO: Check we have enough energy for this recipe.
        if (canTakeOutput(recipe)) {
            currentRecipe = recipe;
            energyConsumed = 0;
            consumeIngredients(recipe);
        }
    }

    protected void clearRecipe() {
        currentRecipe = null;
        energyConsumed = 0;
    }

    // region Crafting Lifecycle

    /**
     * Whether crafting is running.
     */
    protected boolean canCraft() {
        return getCurrentRecipe() != null;
    }

    /**
     * Whether the machine can start a new recipe
     * @return
     */
    protected boolean canSelectRecipe() {
        return true;
    }

    /**
     * Get the cost of crafting this recipe
     */
    protected abstract int getEnergyCost(R recipe);

    /**
     * Select the next recipe for crafting.
     */
    protected void selectNextRecipe() {
        getRecipe().ifPresent(this::setCurrentRecipe);
    }

    // endregion

    // region Recipes

    /**
     * Get the recipe type for recipe lookups.
     */
    protected abstract RecipeType<?> getRecipeType();

    /**
     * Get the currently crafting recipe.
     */
    protected R getCurrentRecipe() {
        return currentRecipe;
    }

    /**
     * Get the recipe from the recipe manager.
     */
    private Optional<R> getRecipe() {
        return level.getRecipeManager().getRecipeFor((RecipeType<R>) getRecipeType(), getRecipeWrapper(), level);
    }

    // endregion

    // region Recipe Crafting

    /**
     * Consume the initial ingredients for a recipe
     */
    protected abstract void consumeIngredients(R recipe);

    /**
     * Whether or not the output of the recipe can be put into the inventory of the machine.
     */
    protected abstract boolean canTakeOutput(R recipe);

    /**
     * Assemble the recipe after it has finished crafting.
     */
    protected abstract void assembleRecipe(R recipe);

    // endregion

    // region Saving

    @Override
    public void saveAdditional(CompoundTag pTag) {
        pTag.putInt("energy_used", energyConsumed);
        if (currentRecipe != null) {
            pTag.putString("recipe", currentRecipe.getId().toString());
        }
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (pTag.contains("recipe")) {
            String recipeLoc = pTag.getString("recipe");

            // TODO: Getting the current recipe like this doesn't work. level is null when the game first loads.
            level.getRecipeManager().byKey(new ResourceLocation(recipeLoc)).ifPresent(recipe -> {
                currentRecipe = (R) recipe;
            });
        }
        super.load(pTag);
    }

    // endregion
}
