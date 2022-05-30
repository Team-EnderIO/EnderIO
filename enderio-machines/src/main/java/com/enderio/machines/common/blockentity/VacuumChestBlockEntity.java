package com.enderio.machines.common.blockentity;

import java.util.function.Predicate;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.VacuumMachineEntity;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MachineInventoryLayout.Builder;
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
        super(pType, pWorldPosition, pBlockState, ItemEntity.class);
    }
    
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new VacuumChestMenu(this, inventory, containerId);
    }
    
    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return extractableGUISlot(MachineInventoryLayout.builder(false),28).build();
    }
    
    @Override
    protected MachineInventory createMachineInventory(MachineInventoryLayout layout) {
        // TODO Auto-generated method stub
        return new MachineInventory(getIOConfig(), layout) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
            
            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot == 27) { //TODO filter slot type
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
            
        };
    }

	@Override
	public void handleEntity(ItemEntity entity) {
		for (int i=0; i<this.getInventory().getSlots();i++) {
            ItemStack reminder = this.getInventory().insertItem(i, entity.getItem().copy(), false);
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

	@Override
	public MachineTier getTier() {
		return MachineTier.Standard;
	}
	
	// Slot config
	
	public Builder extractableGUISlot(Builder builder, int count) {
        for (int i = 0; i < count; i++) {
            builder.slot(slot -> slot.guiInsert().guiExtract().extract());
        }
        return builder;
    }
}
