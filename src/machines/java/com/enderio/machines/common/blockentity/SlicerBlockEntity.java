package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
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
            .inputSlot(6) // Inputs
            .inputSlot(this::validAxe) // Axe
            .inputSlot((slot, stack) -> stack.getItem() instanceof ShearsItem) // Shears
            .setStackLimit(64) // Reset stack limit
            .outputSlot() // Result
            .capacitor()
            .build();
    }

    private boolean validAxe(int slot, ItemStack stack) {
        if (stack.getItem() instanceof AxeItem axeItem) {
            return axeItem.getTier().getLevel() > TierSortingRegistry.getSortedTiers().indexOf(Tiers.WOOD);
        }
        return false;
    }

    @Nullable
    @Override
    protected PoweredCraftingTask<SlicingRecipe, Container> getNewTask() {
        MachineInventory inv = getInventory();
        if (inv.getStackInSlot(6).isEmpty() || inv.getStackInSlot(7).isEmpty())
            return null;
        return super.getNewTask();
    }

    @Override
    protected PoweredCraftingTask<SlicingRecipe, Container> createTask(@Nullable SlicingRecipe recipe) {
        return new PoweredCraftingTask<>(this, getContainer(), 8, recipe) {
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
        };
    }

    @Override
    protected Container getContainer() {
        return container;
    }
}
