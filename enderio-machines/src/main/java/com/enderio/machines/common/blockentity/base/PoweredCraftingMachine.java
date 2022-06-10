package com.enderio.machines.common.blockentity.base;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.machines.recipes.MachineRecipe;
import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PoweredCraftingMachine<R extends MachineRecipe<C>, C extends Container> extends PoweredTaskMachineEntity<PoweredCraftingTask> {
    // Flag for determining if we should re-check for a possible recipe.
    protected boolean inventoryDirty = true;

    public PoweredCraftingMachine(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey, BlockEntityType<?> pType,
        BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);
    }

    @Override
    protected boolean hasNextTask() {
        return inventoryDirty;
    }

    @Override
    protected @Nullable PoweredCraftingTask<R, C> getNextTask() {
        inventoryDirty = false;
        return null;
    }

    @Override
    protected @Nullable PoweredCraftingTask<R, C> loadTask(CompoundTag nbt) {
        return null;
    }

    protected abstract PoweredCraftingTask<R, C> createTask(R recipe);
}
