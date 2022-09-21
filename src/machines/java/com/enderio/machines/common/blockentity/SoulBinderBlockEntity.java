package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.SoulBinderMenu;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

public class SoulBinderBlockEntity extends PoweredCraftingMachine<SoulBindingRecipe, Container> {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);

    private final RecipeWrapper container;

    public SoulBinderBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(MachineRecipes.SOUL_BINDING.type().get(), CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);

        container = new RecipeWrapper(getInventory());
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SoulBinderMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .setStackLimit(1)
            .inputSlot((slot, stack) -> stack.getCapability(EIOCapabilities.ENTITY_STORAGE).isPresent())
            .inputSlot()
            .setStackLimit(64)
            .outputSlot()
            .capacitor()
            .build();
    }

    @Override
    protected PoweredCraftingTask<SoulBindingRecipe, Container> createTask(@Nullable SoulBindingRecipe recipe) {
        return new PoweredCraftingTask<>(this, getContainer(), 2, recipe) {

            @Override
            protected void takeInputs(SoulBindingRecipe recipe) {
                getInventory().getStackInSlot(0).shrink(1);
                getInventory().getStackInSlot(1).shrink(1);
            }
        };
    }

    @Override
    protected Container getContainer() {
        return container;
    }


}
