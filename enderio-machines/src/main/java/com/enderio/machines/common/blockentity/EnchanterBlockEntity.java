package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.MachineItemHandler;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.MachineInventoryLayout;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.api.recipe.EnchanterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnchanterBlockEntity extends MachineBlockEntity {

    public EnchanterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Override
    public MachineTier getTier() {
        return MachineTier.Standard;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .addInput(itemStack -> itemStack.getItem() == Items.WRITABLE_BOOK)
            .addInput()
            .addInput()
            .addOutput()
            .build();
    }

    @Override
    public boolean supportsRedstoneControl() {
        return false;
    }

    @Override
    public boolean supportsIo() {
        return false;
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new EnchanterMenu(this, pInventory, pContainerId);
    }

    @Override
    protected MachineItemHandler createItemHandler(MachineInventoryLayout layout) {
        return new MachineItemHandler(getIoConfig(), layout) {
            protected void onContentsChanged(int slot) {
                if (slot != 3) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, getRecipeWrapper(), level);
                    if (recipe.isPresent()) {
                        getInventory().setStackInSlot(3, recipe.get().assemble(getRecipeWrapper()));
                    }
                    else {
                        getInventory().setStackInSlot(3, ItemStack.EMPTY);
                    }
                }
                setChanged();
            }

            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == 3 && !isServer()) {
                    return ItemStack.EMPTY;
                }
                return super.extractItem(slot, amount, simulate);
            }
        };
    }
    
}
