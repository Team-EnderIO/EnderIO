package com.enderio.machines.common.blockentity;

import java.util.Optional;
import java.util.function.Predicate;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.VacuumMachineEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.menu.VacuumChestMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VacuumChestBlockEntity extends VacuumMachineEntity<ItemEntity> {
    
    public VacuumChestBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineTier.STANDARD, pType, pWorldPosition, pBlockState, ItemEntity.class);
    }
    
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new VacuumChestMenu(this, inventory, containerId);
    }
    
    @Override
    public Optional<ItemSlotLayout> getSlotLayout() {
        return Optional.of(ItemSlotLayout.basic(28,0));
    }
    
    @Override
    protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
        return new ItemHandlerMaster(getIoConfig(), layout) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot == 27) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

	@Override
	public void handleEntity(ItemEntity entity) {
		for (int i=0; i<this.getItemHandler().getSlots();i++) {
            ItemStack reminder = this.getItemHandler().insertItem(i, entity.getItem().copy(), false);
            if (reminder.isEmpty()) {
            	entity.discard();
            	return;
            } else {
            	entity.getItem().setCount(reminder.getCount());
            }
        } 
	}
	
	//TODO filter
	@Override
	public Predicate<ItemEntity> getFilter() {
		return super.getFilter();
	}
}
