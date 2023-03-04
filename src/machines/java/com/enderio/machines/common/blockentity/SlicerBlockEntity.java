package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.SlicerMenu;
import com.enderio.machines.common.recipe.SlicingRecipe;
import net.minecraft.core.BlockPos;
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
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

public class SlicerBlockEntity extends PoweredCraftingMachine<SlicingRecipe, Container> {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);

    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final MultiSlotAccess INPUTS = new MultiSlotAccess();
    public static final SingleSlotAccess AXE = new SingleSlotAccess();
    public static final SingleSlotAccess SHEARS = new SingleSlotAccess();
    private final RecipeWrapper container;

    public SlicerBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(MachineRecipes.SLICING.type().get(), CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);

        container = new RecipeWrapper(getInventory());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SlicerMenu(this, inventory, containerId);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .setStackLimit(1) // Force all input slots to have 1 output
            .inputSlot(6)
            .slotAccess(INPUTS)
            .inputSlot(this::validAxe)
            .slotAccess(AXE)
            .inputSlot((slot, stack) -> stack.getItem() instanceof ShearsItem)
            .slotAccess(SHEARS)
            .setStackLimit(64) // Reset stack limit
            .outputSlot()
            .slotAccess(OUTPUT)
            .capacitor()
            .build();
    }

    private boolean validAxe(int slot, ItemStack stack) {
        if (stack.getItem() instanceof AxeItem axeItem) {
            return TierSortingRegistry.getSortedTiers().indexOf(axeItem.getTier()) > TierSortingRegistry.getSortedTiers().indexOf(Tiers.WOOD);
        }
        return false;
    }

    @Nullable
    @Override
    protected PoweredCraftingTask<SlicingRecipe, Container> getNewTask() {
        MachineInventory inv = getInventory();
        if (AXE.getItemStack(inv).isEmpty() || SHEARS.getItemStack(inv).isEmpty())
            return null;
        return super.getNewTask();
    }

    @Override
    protected PoweredCraftingTask<SlicingRecipe, Container> createTask(@Nullable SlicingRecipe recipe) {
        return new PoweredCraftingTask<>(this, getContainer(), OUTPUT, recipe) {
            @Override
            protected void takeInputs(SlicingRecipe recipe) {
                // Deduct ingredients
                MachineInventory inv = getInventory();
                for (SingleSlotAccess access : INPUTS.getAccesses()) {
                    access.getItemStack(inv).shrink(1);
                }

                AXE.getItemStack(inv).hurt(1, level.getRandom(), null);
                SHEARS.getItemStack(inv).hurt(1, level.getRandom(), null);
            }
        };
    }

    @Override
    protected Container getContainer() {
        return container;
    }
}
