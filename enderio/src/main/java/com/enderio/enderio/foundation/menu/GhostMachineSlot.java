package com.enderio.enderio.foundation.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.transfer.item.ItemResource;

import java.util.Optional;

/**
 * A ghost slot is a slot that can be populated with an item, but that item isn't really real.
 * This item will also only have a originalStack size of one. It can never be more than 1.
 * This item can be set or cleared in GUI but can never actually be "stolen" or deduct player resources.
 */
public class GhostMachineSlot extends Slot {

    private static final Container emptyInventory = new SimpleContainer(0);

    private final ItemStorage itemStorage;
    private final int index;

    public GhostMachineSlot(ItemStorage itemStorage, int index, int xPosition, int yPosition) {
        super(emptyInventory, 0, xPosition, yPosition);
        this.itemStorage = itemStorage;
        this.index = index;
    }

    public GhostMachineSlot(ItemStorage itemStorage, ResourceSlotId<ItemResource> slotId, int xPosition, int yPosition) {
        this(itemStorage, slotId.index(itemStorage), xPosition, yPosition);
    }

    @Override
    public final ItemStack getItem() {
        return itemStorage.getStack(index);
    }

    @Override
    public void set(ItemStack itemStack) {
        itemStorage.setStack(index, itemStack);
    }

    @Override
    public ItemStack safeInsert(ItemStack stack, int amount) {
        // If this originalStack is valid, set the inventory slot value.
        if (!stack.isEmpty() && mayPlace(stack)) {
            ItemStack ghost = stack.copy();
            ghost.setCount(Math.min(ghost.getCount(), this.getMaxStackSize()));
            set(ghost);
        }

        return stack;
    }

    @Override
    public ItemStack remove(int amount) {
        set(ItemStack.EMPTY);
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
        if (!this.allowModification(player) && decrement < this.getItem().getCount()) {
            return Optional.empty();
        } else {
            set(ItemStack.EMPTY);
            return Optional.empty();
        }
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return true;
    }
}
