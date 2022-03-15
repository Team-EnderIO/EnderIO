package com.enderio.machines.common.blockentity;

import java.util.Optional;

import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PowerConsumingMachineEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.menu.ImpulseHopperMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class ImpulseHopperBlockEntity extends PowerConsumingMachineEntity{
	private static final int IMPULSE_HOPPER_POWER_USE_PER_ITEM = 10; //TODO config?
	private ItemStackHandler ghosthandler = new ItemStackHandler(6);

	public ImpulseHopperBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition,
			BlockState pBlockState) {
		super(MachineTier.SIMPLE, MachineBlockEntities.IMPULSE_HOPPER.get(), pWorldPosition, pBlockState);
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new ImpulseHopperMenu(this, inventory, containerId);
	}
	
	@Override
	public Optional<ItemSlotLayout> getSlotLayout() {
		return Optional.of(ItemSlotLayout.withCapacitor(6, 6));
	}
	
	@Override
	protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
		return new ItemHandlerMaster(getIoConfig(), layout);
	}
	
	public ItemStackHandler getGhosthandler() {
		return ghosthandler;
	}
	
	@Override
	public void tick() {
		super.tick();
		if(ShouldActTick() && shouldPassItems()) {
			passItems();
		}
	}
	
	public boolean ShouldActTick() {// TODO General tick method for power consuming devices?
		return shouldAct() && level.getGameTime() % ticksForAction() == 0;
	}
	
	public int ticksForAction() {
		return 20; //TODO Speed modifier
	}
	
	public boolean canPass(int slot) {
		if (this.getItemHandler().getStackInSlot(slot).getItem().equals(this.getGhosthandler().getStackInSlot(slot).getItem()) || this.getGhosthandler().getStackInSlot(slot).isEmpty()) {
			if (this.getItemHandler().getStackInSlot(slot).getCount() >= this.getGhosthandler().getStackInSlot(slot).getCount()) {
				return true;
			}
		}
		return false;
	}
	
	public boolean shouldPassItems() {
		int totalpower = 0;
		for (int i = 0; i < this.getGhosthandler().getSlots(); i++) {
			if (canPass(i)) {
				totalpower += this.getGhosthandler().getStackInSlot(i).getCount() * IMPULSE_HOPPER_POWER_USE_PER_ITEM;
				continue;
			}
			return false;
		}
		if (canConsumeEnergy(totalpower)) {
			return true;
		}
		return false;
	}
	
	public void passItems() {
		for (int i = 0; i < this.getGhosthandler().getSlots(); i++) {
			ItemStack stack = this.getItemHandler().getStackInSlot(i);
			ItemStack ghost = this.getGhosthandler().getStackInSlot(i);
			ItemStack result = this.getItemHandler().getStackInSlot(i + 6);
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
			this.consumeEnergy(ghost.getCount());
			stack.shrink(ghost.getCount());
			this.getItemHandler().setStackInSlot(i + 6, result);
		}
	}

}
