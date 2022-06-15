package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.base.common.blockentity.sync.FloatDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.api.UseOnly;
import com.enderio.machines.common.block.ProgressMachineBlock;
import com.enderio.api.recipe.IMachineRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.LogicalSide;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

// TODO: I wanna turn crafting into a task based system, so we can have PoweredProgressMachineEntity that takes a task and performs it. Means less base-class nonsense
//       This is a job for the sagmill branch.
public abstract class PoweredCraftingMachineEntity<R extends Recipe<Container>> extends PowerConsumingMachineEntity {
    private int energyConsumed;
    private R currentRecipe;

    @Nullable private ResourceLocation loadedRecipe = null;

    @UseOnly(LogicalSide.CLIENT) private float clientProgress;

    public PoweredCraftingMachineEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey consumptionKey, BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, consumptionKey, pType, pWorldPosition, pBlockState);

        // Sync machine progress to the client.
        addDataSlot(new FloatDataSlot(this::getProgress, p -> clientProgress = p, SyncMode.GUI));
    }

    public float getProgress() {
        if (level.isClientSide)
            return clientProgress;
        if (getCurrentRecipe() == null)
            return 0;
        return energyConsumed / (float) getEnergyCost(getCurrentRecipe());
    }

    @Override
    public void serverTick() {
        boolean active = false;

        if (canAct()) {
            // If we've been asked to load a recipe (from NBT load usually), do it.
            if (loadedRecipe != null) {
                processLoadedRecipe();
            }

            if (canCraft()) {
                int cost = getEnergyCost(getCurrentRecipe());
                if (energyConsumed <= cost) {
                    // Attempt to consume the rest of the required energy.
                    energyConsumed += consumeEnergy(cost - energyConsumed);
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
        if (getBlockState().getValue(ProgressMachineBlock.POWERED) != active) {
            level.setBlock(getBlockPos(), getBlockState().setValue(ProgressMachineBlock.POWERED, active), Block.UPDATE_ALL);
        }

        super.serverTick();
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

    // TODO: Do we add capacitor requirement logic in here? Probably. I'll do it when I add the SAG mill.

    /**
     * Whether crafting is running.
     */
    protected boolean canCraft() {
        return getCurrentRecipe() != null && getEnergyStorage().getEnergyStored() > 0;
    }

    /**
     * Whether the machine can start a new recipe
     *
     * @return
     */
    protected boolean canSelectRecipe() {
        return getEnergyStorage().getEnergyStored() > 0; // Need some energy, stops from consuming the resources
    }

    /**
     * Get the cost of crafting this recipe
     */
    protected int getEnergyCost(R recipe) {
        if (recipe instanceof IMachineRecipe<?, ?> machineRecipe) {
            return machineRecipe.getEnergyCost();
        }
        throw new NotImplementedException("Machine must implement getEnergyCost for types not implementing MachineRecipe");
    }

    /**
     * Select the next recipe for crafting.
     */
    protected void selectNextRecipe() {
        findMatchingRecipe().ifPresent(this::setCurrentRecipe);
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
    public R getCurrentRecipe() {
        return currentRecipe;
    }

    /**
     * Get the recipe from the recipe manager.
     */
    private Optional<R> findMatchingRecipe() {
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
        energyConsumed = pTag.getInt("energy_used");
        if (pTag.contains("recipe")) {
            // Save the recipe for later when the level is loaded
            loadedRecipe = new ResourceLocation(pTag.getString("recipe"));
        }
        super.load(pTag);
    }

    protected void processLoadedRecipe() {
        // Try to find the recipe.
        level.getRecipeManager().byKey(loadedRecipe).ifPresent(recipe -> {
            try {
                currentRecipe = (R) recipe;
            } catch (ClassCastException ex) {
                // Do nothing. Forget the recipe existed.
            }
        });

        // We've tried loading the recipe now, forget it.
        loadedRecipe = null;
    }

    // endregion
}
