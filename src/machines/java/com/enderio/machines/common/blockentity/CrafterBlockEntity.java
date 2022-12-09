package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.CrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class CrafterBlockEntity extends PoweredMachineEntity {

    //TODO Change values
    public static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable ENERGY_TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 10f);
    private static final int ENERGY_USAGE_PER_ITEM = 10;

    private static final CraftingContainer dummyCraftingContainer = new CraftingContainer(new AbstractContainerMenu(null, -1) {
        @Override
        public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player pPlayer) {
            return false;
        }
    }, 3, 3);

    public CrafterBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_TRANSFER, ENERGY_USAGE, type, worldPosition, blockState);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CrafterMenu(this, inventory, containerId);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().capacitor().inputSlot(9).outputSlot(1).ghostSlot(9).previewSlot().build();
    }

    @Override
    public void serverTick() {
        Optional<CraftingRecipe> opt = getRecipe();
        if (opt.isPresent()) {
            CraftingRecipe recipe = opt.get();
            this.getInventory().setStackInSlot(20, recipe.getResultItem());
            if (shouldActTick()) {
                //craft item
            }
        } else {
            this.getInventory().setStackInSlot(20, ItemStack.EMPTY);
        }
        super.serverTick();
    }

    public boolean shouldActTick() {
        return canAct() && level.getGameTime() % ticksForAction() == 0;
    }

    public int ticksForAction() {
        return 20;
    }

    private Optional<CraftingRecipe> getRecipe() {
        var inv = this.getInventory();
        int start = 11;
        for (int i = 0; i < 9; i++) {
            dummyCraftingContainer.setItem(i, inv.getStackInSlot(start + i));
        }
        return getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, dummyCraftingContainer, getLevel());
    }
}
