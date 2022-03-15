package com.enderio.machines.common.menu;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class GhostSlot extends SlotItemHandler{

	public GhostSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}
	
	@Override
	public ItemStack remove(int amount) {
		super.remove(amount);
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack safeInsert(ItemStack p_150657_, int p_150658_) {
		super.safeInsert(p_150657_.copy(), p_150658_);
		return p_150657_;
	}
	
	@Override
	public ItemStack safeTake(int p_150648_, int p_150649_, Player p_150650_) {
		super.safeTake(p_150648_, p_150649_, p_150650_);
		return ItemStack.EMPTY;
	}
	
}
