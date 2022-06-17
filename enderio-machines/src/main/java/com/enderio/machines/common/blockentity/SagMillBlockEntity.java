package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.SagMillMenu;
import com.enderio.machines.common.recipe.SagMillingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class SagMillBlockEntity extends PoweredCraftingMachine<SagMillingRecipe, SagMillingRecipe.Container> {

    // region Tiers

    // TODO: Simple and Enhanced.

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

    // endregion

    private final SagMillingRecipe.Container container;

    // TODO: Grinding ball data and durability.

    public SagMillBlockEntity(CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey, BlockEntityType<?> pType, BlockPos pWorldPosition,
        BlockState pBlockState) {
        super(MachineRecipes.Types.SAGMILLING, capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);
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
    protected PoweredCraftingTask<SagMillingRecipe, SagMillingRecipe.Container> createTask(@Nullable SagMillingRecipe recipe) {
        return new PoweredCraftingTask<>(this, container, 1, 4, recipe) {
            @Override
            protected void takeInputs(SagMillingRecipe recipe) {
                getInventory().getStackInSlot(0).shrink(1);
            }
        };
    }

    @Override
    protected SagMillingRecipe.Container getContainer() {
        return container;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SagMillMenu(this, inventory, containerId);
    }
}
