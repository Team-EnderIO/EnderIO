package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PowerConsumingMachineEntity;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.ImpulseHopperMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ImpulseHopperBlockEntity extends PowerConsumingMachineEntity{
	private static final int IMPULSE_HOPPER_POWER_USE_PER_ITEM = 10; //TODO config?

	public ImpulseHopperBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
		super(MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new ImpulseHopperMenu(this, inventory, containerId);
	}
	
	@Override
	public MachineInventoryLayout getInventoryLayout() {
	    return MachineInventoryLayout.builder(true).inputSlot(6).outputSlot(6).ghostSlot(6).build(); // first 6 input, second 6 output, third 6 ghost
	}
	
	@Override
	public void serverTick() {
		super.serverTick();
		if(ShouldActTick() && shouldPassItems()) {
			passItems();
		}
	}
	
	public boolean ShouldActTick() {// TODO General tick method for power consuming devices?
		return canAct() && level.getGameTime() % ticksForAction() == 0;
	}
	
	public int ticksForAction() {
		return 20; //TODO Speed modifier
	}
	
	public boolean canPass(int slot) {
		if (this.getInventory().getStackInSlot(slot).getItem().equals(this.getInventory().getStackInSlot(slot+6+6).getItem()) || this.getInventory().getStackInSlot(slot+6+6).isEmpty()) {
			if (this.getInventory().getStackInSlot(slot).getCount() >= this.getInventory().getStackInSlot(slot+6+6).getCount()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean shouldPassItems() {
		int totalpower = 0;
		for (int i = 0; i < 6; i++) {
			if (canPass(i)) {
				totalpower += this.getInventory().getStackInSlot(i+6+6).getCount() * IMPULSE_HOPPER_POWER_USE_PER_ITEM;
				continue;
			}
			return false;
		}
//		if (canConsumeEnergy(totalpower)) {
//			return true;
//		}
		return false;
	}
	
	public void passItems() {
		for (int i = 0; i < 6; i++) {
			ItemStack stack = this.getInventory().getStackInSlot(i);
			ItemStack ghost = this.getInventory().getStackInSlot(i+6+6);
			ItemStack result = this.getInventory().getStackInSlot(i + 6);
			if (ghost.isEmpty()) {
				continue;
			}
			if (result.isEmpty()) {
				result = stack.copy();
				result.setCount(ghost.getCount());
			} else if (stack.is(result.getItem())) {
				result.setCount(result.getCount() + ghost.getCount());
			} else {
				continue;
			}
//			this.consumeEnergy(ghost.getCount());
			stack.shrink(ghost.getCount());
			this.getInventory().setStackInSlot(i + 6, result);
		}
	}

    @Override
    public MachineTier getTier() {
        return MachineTier.STANDARD;
    }

}
