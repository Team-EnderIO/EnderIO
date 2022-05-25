package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.data.sidecontrol.item.MachineItemHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class MachineSlot extends SlotItemHandler {

    public MachineSlot(MachineItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    protected MachineItemHandler getMasterInventory() {
        return (MachineItemHandler) getItemHandler();
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !getMasterInventory().guiExtractItem(getSlotIndex(), 1, true).isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        return getMasterInventory().guiExtractItem(getSlotIndex(), amount, false);
    }
}
