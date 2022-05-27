package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.energy.EnergyIOMode;
import com.enderio.api.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class NewAlloySmelterBlockEntity extends PoweredTaskMachineEntity<PoweredCraftingTask<AlloySmeltingRecipe>> {
    public static class Standard extends NewAlloySmelterBlockEntity {

        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.Standard;
        }
    }

    public NewAlloySmelterBlockEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey,
        BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        // Setup item slots
        return MachineInventoryLayout.builder()
            .addInputs(3, this::acceptSlotInput)
            .addOutput()
            .capacitor(() -> getTier() != MachineTier.Simple)
            .build();
    }

    private boolean acceptSlotInput(int slot, ItemStack stack) {
        // TODO
        return true;
    }

    @Override
    protected @Nullable PoweredCraftingTask<AlloySmeltingRecipe> getNextTask() {
        if (level == null)
            return null;

        // TODO: wrapping and handling of smelting recipes.

        return level
            .getRecipeManager()
            .getRecipeFor(MachineRecipes.Types.ALLOY_SMELTING, getRecipeWrapper(), level)
            .map(this::createTask)
            .orElse(null);
    }

    private PoweredCraftingTask<AlloySmeltingRecipe> createTask(AlloySmeltingRecipe recipe) {
        // TODO: Nice system for consuming inputs
        for (int i = 0; i < 3; i++) {
            recipe.consumeInput(getInventory().getStackInSlot(i));
        }

        return new PoweredCraftingTask<>(energyStorage, recipe, getRecipeWrapper()) {
            @Override
            protected boolean takeOutputs(List<ItemStack> outputs) {
                // Alloy smelting recipes only have a single output
                ItemStack out = outputs.get(0);

                MachineInventory inv = getInventory();

                if (inv.forceInsertItem(3, out, true).isEmpty()) {
                    inv.forceInsertItem(3, out, false);
                    return true;
                }

                return false;
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(this, inventory, containerId);
    }
}
