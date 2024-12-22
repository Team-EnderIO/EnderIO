package com.enderio.machines.common.blocks.base.task;

import com.enderio.machines.common.blocks.base.MachineRecipe;
import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PoweredCraftingMachineTask<R extends MachineRecipe<C>, C extends RecipeInput>
        extends CraftingMachineTask<R, C>
        implements PoweredMachineTask {

    private final IMachineEnergyStorage energyStorage;

    public PoweredCraftingMachineTask(@NotNull Level level, MachineInventory inventory,
            IMachineEnergyStorage energyStorage, C container, MultiSlotAccess outputSlots,
            @Nullable RecipeHolder<R> recipe) {
        super(level, inventory, container, outputSlots, recipe);
        this.energyStorage = energyStorage;
    }

    public PoweredCraftingMachineTask(@NotNull Level level, MachineInventory inventory,
            IMachineEnergyStorage energyStorage, C container, SingleSlotAccess outputSlot,
            @Nullable RecipeHolder<R> recipe) {
        this(level, inventory, energyStorage, container, outputSlot.wrapToMulti(), recipe);
    }

    @Override
    public IMachineEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected int makeProgress(int remainingProgress) {
        return energyStorage.consumeEnergy(remainingProgress, false);
    }

    @Override
    protected int getProgressRequired(R recipe) {
        return recipe.getEnergyCost(recipeInput);
    }
}
