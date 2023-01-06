package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.io.item.MachineInventory;
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

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

public class CrafterBlockEntity extends PoweredMachineEntity {

    //TODO Change values
    public static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable ENERGY_TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 10f);
    private static final int ENERGY_USAGE_PER_ITEM = 10;

    private CraftingRecipe recipe;
    private Queue<ItemStack> outputBuffer = new ArrayDeque<>();

    private static final CraftingContainer dummyCContainer = new CraftingContainer(new AbstractContainerMenu(null, -1) {
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
        return MachineInventoryLayout
            .builder()
            .capacitor()
            .setStackLimit(1)
            .inputSlot(9, this::acceptSlotInput)
            .setStackLimit(64)
            .outputSlot(1)
            .ghostSlot(9)
            .previewSlot()
            .build();
    }

    private boolean acceptSlotInput(int slot, ItemStack stack) {
        return this.getInventory().getStackInSlot(slot + 10).sameItem(stack);
    }

    @Override
    public void serverTick() {
        tryCraft();
        super.serverTick();
        processOutputBuffer();
    }

    private void tryCraft() {
        Optional<ItemStack> opt = getRecipeResult();
        if (opt.isPresent()) {
            ItemStack result = opt.get();
            this.getInventory().setStackInSlot(20, result);
            if (shouldActTick() && hasPowerToCraft() && canMergeOutput(result) && outputBuffer.isEmpty()) {
                craftItem();
            }
        } else {
            this.getInventory().setStackInSlot(20, ItemStack.EMPTY);
        }
    }

    private boolean shouldActTick() {
        return canAct() && level.getGameTime() % ticksForAction() == 0;
    }

    private int ticksForAction() {
        return 20;
    }

    private boolean hasPowerToCraft() {
        return this.energyStorage.consumeEnergy(ENERGY_USAGE_PER_ITEM, true) > 0;
    }

    private void processOutputBuffer() {
        if (outputBuffer.isEmpty()) {
            return;
        }

        // output
        if (canMergeOutput(outputBuffer.peek())) {
            var stack = getInventory().getStackInSlot(10);
            if (stack.isEmpty()) {
                getInventory().setStackInSlot(10, outputBuffer.peek().copy());
            } else {
                stack.grow(outputBuffer.peek().getCount());
            }
            outputBuffer.remove();
        }
    }

    private Optional<ItemStack> getRecipeResult() {
        MachineInventory inv = this.getInventory();
        int start = 11;
        for (int i = 0; i < 9; i++) {
            dummyCContainer.setItem(i, inv.getStackInSlot(start + i).copy());
        }
        Optional<CraftingRecipe> opt = getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, dummyCContainer, getLevel());
        if (opt.isPresent()) {
            recipe = opt.get();
            return Optional.of(recipe.assemble(dummyCContainer));
        }
        return Optional.empty();
    }

    private boolean canMergeOutput(ItemStack item) {
        ItemStack output = this.getInventory().getStackInSlot(10);
        return output.isEmpty() || (ItemStack.isSameItemSameTags(output, item) && (output.getCount() + item.getCount() <= 64));
    }

    private void craftItem() {
        MachineInventory inv = this.getInventory();
        int start = 1;
        for (int i = 0; i < 9; i++) {
            if (!ItemStack.isSame(inv.getStackInSlot(i + start), (inv.getStackInSlot(i + start + 10)))) {
                return;
            }
        }
        //copy input items
        for (int i = 0; i < 9; i++) {
            dummyCContainer.setItem(i, inv.getStackInSlot(start + i).copy());
        }
        //craft
        clearInput();
        outputBuffer.add(recipe.assemble(dummyCContainer));
        outputBuffer.addAll(recipe.getRemainingItems(dummyCContainer));
        // clean buffer
        outputBuffer.removeIf(ItemStack::isEmpty);

    }

    private void clearInput() {
        int start = 1;
        for (int i = 0; i < 9; i++) {
            getInventory().setStackInSlot(start + i, ItemStack.EMPTY);
        }
    }
}
