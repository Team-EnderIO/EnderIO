package com.enderio.machines.common.blockentity;

import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.SlicerMenu;
import com.enderio.machines.common.recipe.SlicingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.TierSortingRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SlicerBlockEntity extends PoweredTaskMachineEntity<PoweredCraftingTask<SlicingRecipe, Container>> {

    private boolean inventoryChanged = true;

    public SlicerBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition,
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

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder(true)
            .setStackLimit(1) // Force all input slots to have 1 output
            .inputSlot(6) // Inputs
            .inputSlot(this::validAxe) // Axe
            .inputSlot((slot, stack) -> stack.getItem() instanceof ShearsItem) // Shears
            .setStackLimit(64) // Reset stack limit
            .outputSlot() // Result
            .build();
    }

    private boolean validAxe(int slot, ItemStack stack) {
        if (stack.getItem() instanceof AxeItem axeItem) {
            return axeItem.getTier().getLevel() > TierSortingRegistry.getSortedTiers().indexOf(Tiers.WOOD);
        }
        return false;
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
    protected @Nullable PoweredCraftingTask<SlicingRecipe, Container> getNextTask() {
        inventoryChanged = false;

        // If we have not got a tool in each slot, skip
        MachineInventory inv = getInventory();
        if (inv.getStackInSlot(6).isEmpty() || inv.getStackInSlot(7).isEmpty())
            return null;

        return level
            .getRecipeManager()
            .getRecipeFor(MachineRecipes.Types.SLICING, getRecipeWrapper(), level)
            .map(Task::new)
            .orElse(null);
    }

    @Override
    protected @Nullable PoweredCraftingTask<SlicingRecipe, Container> loadTask(CompoundTag nbt) {
        Task task = new Task(null);
        task.deserializeNBT(nbt);
        return task;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SlicerMenu(this, inventory, containerId);
    }

    private class Task extends PoweredCraftingTask<SlicingRecipe, Container> {

        public Task(@Nullable SlicingRecipe recipe) {
            super(getEnergyStorage(), recipe, level, getRecipeWrapper());
        }

        @Override
        protected void takeInputs(SlicingRecipe recipe) {
            // Deduct ingredients
            MachineInventory inv = getInventory();
            for (int i = 0; i < 6; i++) {
                inv.getStackInSlot(i).shrink(1);
            }

            // Damage tools
            for (int i = 6; i < 8; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                stack.setDamageValue(stack.getDamageValue() + 1);
            }
        }

        @Override
        protected boolean takeOutputs(List<OutputStack> outputs, boolean simulate) {
            // Copied from sagmill as its easy

            // Get outputs
            MachineInventory inv = getInventory();

            // See that we can add all of the outputs
            for (OutputStack output : outputs) {
                ItemStack item = output.getItem();

                // Try putting some in each slot.
                for (int i = 8; i < 9; i++) {
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
                    for (int i = 8; i < 9; i++) {
                        item = inv.insertItem(i, item, false);
                    }
                }
            }

            return true;
        }
    }
}
