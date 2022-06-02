package com.enderio.machines.common.menu;

import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * A ghost slot is a slot that can be populated with an item, but that item isn't really real.
 * This item will also only have a stack size of one. It can never be more than 1.
 * This item can be set or cleared in GUI but can never actually be "stolen" or deduct player resources.
 */
public class GhostMachineSlot extends MachineSlot {
    public GhostMachineSlot(MachineInventory itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);

        // Check config, we need to get this right or bad stuff will happen.
        MachineInventoryLayout layout = itemHandler.getLayout();
        if (layout.canInsert(index) || layout.canExtract(index))
            throw new RuntimeException("Ghost slot can be externally modified!!");
        if (!layout.guiCanInsert(index) || !layout.guiCanExtract(index))
            throw new RuntimeException("Ghost slot cannot be modified by the player!");
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int amount) {
        // If this stack is valid, set the inventory slot value.
        if (!stack.isEmpty() && mayPlace(stack)) {
            ItemStack ghost = stack.copy();
            ghost.setCount(1);
            set(ghost);
        }

        return stack;
    }

    @NotNull
    @Override
    public ItemStack remove(int amount) {
        set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }
}
