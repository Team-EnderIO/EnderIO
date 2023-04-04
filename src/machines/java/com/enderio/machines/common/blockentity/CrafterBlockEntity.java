package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
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

    public static final MultiSlotAccess INPUT = new MultiSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final MultiSlotAccess GHOST = new MultiSlotAccess();
    public static final SingleSlotAccess PREVIEW = new SingleSlotAccess();

    private CraftingRecipe recipe;
    private final Queue<ItemStack> outputBuffer = new ArrayDeque<>();

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
            .slotAccess(INPUT)
            .setStackLimit(64)
            .outputSlot(1)
            .slotAccess(OUTPUT)
            .setStackLimit(1)
            .ghostSlot(9)
            .slotAccess(GHOST)
            .previewSlot()
            .slotAccess(PREVIEW)
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
            PREVIEW.setStackInSlot(this, result);
            if (shouldActTick() && hasPowerToCraft() && canMergeOutput(result) && outputBuffer.isEmpty()) {
                craftItem();
            }
        } else {
            PREVIEW.setStackInSlot(this, ItemStack.EMPTY);
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
            var stack = OUTPUT.getItemStack(this);
            if (stack.isEmpty()) {
                OUTPUT.setStackInSlot(this, outputBuffer.peek().copy());
            } else {
                stack.grow(outputBuffer.peek().getCount());
            }
            outputBuffer.remove();
        }
    }

    private Optional<ItemStack> getRecipeResult() {
        for (int i = 0; i < 9; i++) {
            dummyCContainer.setItem(i, GHOST.get(i).getItemStack(this).copy());
        }
        Optional<CraftingRecipe> opt = getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, dummyCContainer, getLevel());
        if (opt.isPresent()) {
            recipe = opt.get();
            return Optional.of(recipe.assemble(dummyCContainer));
        }
        return Optional.empty();
    }

    private boolean canMergeOutput(ItemStack item) {
        ItemStack output = OUTPUT.getItemStack(this);
        return output.isEmpty() || (ItemStack.isSameItemSameTags(output, item) && (output.getCount() + item.getCount() <= 64));
    }

    private void craftItem() {
        for (int i = 0; i < 9; i++) {
            if (!ItemStack.isSame(INPUT.get(i).getItemStack(this), GHOST.get(i).getItemStack(this))) {
                return;
            }
        }
        //copy input items
        for (int i = 0; i < 9; i++) {
            dummyCContainer.setItem(i, INPUT.get(i).getItemStack(this).copy());
        }
        //craft
        clearInput();
        outputBuffer.add(recipe.assemble(dummyCContainer));
        outputBuffer.addAll(recipe.getRemainingItems(dummyCContainer));
        // clean buffer
        outputBuffer.removeIf(ItemStack::isEmpty);

    }

    private void clearInput() {
        for (int i = 0; i < 9; i++) {
            INPUT.get(i).setStackInSlot(this, ItemStack.EMPTY);
        }
    }
}
