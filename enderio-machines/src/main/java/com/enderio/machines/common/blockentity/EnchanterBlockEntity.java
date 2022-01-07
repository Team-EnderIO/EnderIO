package com.enderio.machines.common.blockentity;

import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import com.enderio.machines.common.init.MachineRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnchanterBlockEntity extends MachineBlockEntity {

    public EnchanterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineTier.Standard, pType, pWorldPosition, pBlockState);
    }
    
    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
    }
    
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new EnchanterMenu(this, pInventory, pContainerId);
    }

    @Override
    public Optional<ItemSlotLayout> getSlotLayout() {
        return Optional.of(ItemSlotLayout.basic(3, 1));
    }

    @Override
    protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
        return new ItemHandlerMaster(getIoConfig(), layout) {
            protected void onContentsChanged(int slot) {
                if (slot != 3) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, getRecipeWrapper(), level);
                    if (recipe.isPresent()) {
                        getItemHandler().setStackInSlot(3, recipe.get().assemble(getRecipeWrapper()));
                    }
                    else {
                        getItemHandler().setStackInSlot(3, ItemStack.EMPTY);
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
    
    @Override
    public void setRedstoneControl(RedstoneControl redstoneControl) {
        setChanged();
        super.setRedstoneControl(redstoneControl);
    }
    
}
