package com.enderio.machines.common.blockentity;

import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import com.enderio.machines.common.recipe.MachineRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.List;
import java.util.Optional;

public class EnchanterBlockEntity extends MachineBlockEntity {

    private ItemHandlerMaster itemHandlerMaster = new ItemHandlerMaster(getConfig(), 4, List.of(0,1,2), List.of(3)) {
        protected void onContentsChanged(int slot) {
            if (slot != 3) {
                Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(itemHandlerMaster), level);
                if (recipe.isPresent()) {
                    itemHandlerMaster.setStackInSlot(3, recipe.get().assemble(new RecipeWrapper(itemHandlerMaster)));
                }
                else {
                    itemHandlerMaster.setStackInSlot(3, ItemStack.EMPTY);
                }
            }
            setChanged();
        }
        
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 3 && !isServer()) {
                return ItemStack.EMPTY;
            }
            return super.extractItem(slot, amount, simulate);
        }
    };
    
    public EnchanterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineTier.Standard, pType, pWorldPosition, pBlockState);
    }
    
    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("Items", itemHandlerMaster.serializeNBT());
    }
    
    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandlerMaster.deserializeNBT(pTag.getCompound("Items"));
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new EnchanterMenu(this, pInventory, pContainerId);
    }

    @Override
    public ItemHandlerMaster getItemHandlerMaster() {
        return itemHandlerMaster;
    }
    
    @Override
    public void setRedstoneControl(RedstoneControl redstoneControl) {
        setChanged();
        super.setRedstoneControl(redstoneControl);
    }
    
}
