package com.enderio.enderio.foundation.task;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.enderio.foundation.MachineRecipe;
import com.enderio.enderio.foundation.energy.MachineEnergyHandler;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.MultiSlotAccess;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public abstract class PoweredCraftingMachineTask<R extends MachineRecipe<C>, C extends RecipeInput>
        extends CraftingMachineTask<R, C>
        implements PoweredMachineTask {

    private final MachineEnergyHandler energyStorage;

    public PoweredCraftingMachineTask(@NonNull Level level, MachineInventory inventory,
        @Nullable ResourceStorage<FluidResource> fluidStorage, MachineEnergyHandler energyStorage, C container, MultiSlotAccess outputSlots,
        @Nullable RecipeHolder<R> recipe) {
        super(level, inventory, fluidStorage, container, outputSlots, recipe);
        this.energyStorage = energyStorage;
    }

    public PoweredCraftingMachineTask(@NonNull Level level, MachineInventory inventory,
        MachineEnergyHandler energyStorage, C container, MultiSlotAccess outputSlots,
        @Nullable RecipeHolder<R> recipe) {
        this(level, inventory, null, energyStorage, container, outputSlots, recipe);
    }

    public PoweredCraftingMachineTask(@NonNull Level level, MachineInventory inventory,
        MachineEnergyHandler energyStorage, C container, SingleSlotAccess outputSlot,
        @Nullable RecipeHolder<R> recipe) {
        this(level, inventory, energyStorage, container, outputSlot.wrapToMulti(), recipe);
    }

    @Override
    public MachineEnergyHandler getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected int makeProgress(int remainingProgress) {
        return energyStorage.consume(remainingProgress, null);
    }

    @Override
    protected int getProgressRequired(R recipe) {
        return recipe.getEnergyCost(recipeInput);
    }
}
