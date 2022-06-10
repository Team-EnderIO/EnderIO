package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.SagMillMenu;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class SagMillBlockEntity extends PoweredTaskMachineEntity<PoweredCraftingTask<SagMillingRecipe, SagMillingRecipe.Container>> {
    public static class Standard extends SagMillBlockEntity {

        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition,
            BlockState pBlockState) {
            super(MachineCapacitorKeys.SAG_MILL_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SAG_MILL_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SAG_MILL_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.STANDARD;
        }
    }

    private final SagMillingRecipe.Container container;

    private boolean inventoryChanged = true;

    // TODO: Grinding ball data and durability.

    public SagMillBlockEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey, BlockEntityType<?> pType, BlockPos pWorldPosition,
        BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);
        container = new SagMillingRecipe.Container(getInventory());
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder(getTier() != MachineTier.SIMPLE)
            .inputSlot()
            .outputSlot(4)
            .inputSlot((slot, stack) -> true) // TODO: Check this is actually a grinding ball.
            .build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        inventoryChanged = true; // TODO: 28/05/2022 This kind of thing might be a good idea for a base crafter class?
        super.onInventoryContentsChanged(slot);
    }

    @Override
    protected boolean hasNextTask() {
        return inventoryChanged;
    }

    @Override
    protected @Nullable PoweredCraftingTask<SagMillingRecipe, SagMillingRecipe.Container> getNextTask() {
        inventoryChanged = false;
        return level
            .getRecipeManager()
            .getRecipeFor(MachineRecipes.Types.SAGMILLING, container, level)
            .map(Task::new)
            .orElse(null);
    }

    @Override
    protected @Nullable PoweredCraftingTask<SagMillingRecipe, SagMillingRecipe.Container> loadTask(CompoundTag nbt) {
        Task task = new Task(null);
        task.deserializeNBT(nbt);
        return task;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SagMillMenu(this, inventory, containerId);
    }

    private class Task extends PoweredCraftingTask<SagMillingRecipe, SagMillingRecipe.Container> {

        public Task(@Nullable SagMillingRecipe recipe) {
            super(getEnergyStorage(), recipe, level, container);
        }

        @Override
        protected void takeInputs(SagMillingRecipe recipe) {
            getInventory().getStackInSlot(0).shrink(1);

            // TODO: Take grindingball if present.
        }

        @Override
        protected boolean takeOutputs(List<OutputStack> outputs, boolean simulate) {
            // Get outputs
            MachineInventory inv = getInventory();

            // See that we can add all of the outputs
            for (OutputStack output : outputs) {
                ItemStack item = output.getItem();

                // Try putting some in each slot.
                for (int i = 1; i < 5; i++) {
                    item = inv.insertItem(i, item, true);
                }

                // If we fail, say we can't accept these outputs
                if (!item.isEmpty())
                    return false;
            }

            // If we're not simulating, go for it
            if (!simulate) {
                for (OutputStack output : outputs) {
                    ItemStack item = output.getItem();

                    // Try putting some in each slot.
                    for (int i = 1; i < 5; i++) {
                        item = inv.insertItem(i, item, false);
                    }
                }
            }

            return true;
        }
    }
}
