package com.enderio.machines.common.menu;

import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class MachineSlot extends SlotItemHandler {

    public MachineSlot(MachineInventory itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public MachineSlot(MachineInventory itemHandler, SingleSlotAccess access, int xPosition, int yPosition) {
        super(itemHandler, access.getIndex(), xPosition, yPosition);
    }

    @Override
    public MachineInventory getItemHandler() {
        return (MachineInventory) super.getItemHandler();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return getItemHandler().getLayout().guiCanInsert(this.getSlotIndex()) && super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return getItemHandler().getLayout().guiCanExtract(this.getSlotIndex()) && super.mayPickup(playerIn);
    }

    public boolean canQuickInsertStack() {
        return getItemHandler().getLayout().guiCanInsert(getSlotIndex());
    }
}
