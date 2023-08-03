package com.enderio.machines.common.menu;

import com.enderio.core.common.menu.SyncedMenu;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public abstract class MachineMenu<T extends MachineBlockEntity> extends SyncedMenu<T> {

    protected MachineMenu(@Nullable T blockEntity, Inventory inventory, @Nullable MenuType<?> pMenuType, int pContainerId) {
        super(blockEntity, inventory, pMenuType, pContainerId);
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return getBlockEntity() != null
            && getBlockEntity().stillValid(pPlayer);
    }

    // Stop clearing ghost slot when double clicking.
    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (slot instanceof GhostMachineSlot)
            return false;
        return super.canTakeItemForPickAll(stack, slot);
    }

    // Stop drag-splitting into a ghost slot.
    @Override
    public boolean canDragTo(Slot slot) {
        if (slot instanceof GhostMachineSlot)
            return false;
        return super.canDragTo(slot);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);

        // Clear the ghost slot but don't actually take the stack.
        if (slot instanceof GhostMachineSlot) {
            slot.set(ItemStack.EMPTY);
            return itemstack;
        }

        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < this.slots.size() - 36) {
                if (!this.moveItemStackTo(itemstack1, this.slots.size() - 36, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    // Sorry, but this is the best way I could think of for ignoring ghost slots
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while(!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.slots.get(i);

                // Do not insert into ghost slots
                if (!(slot instanceof GhostMachineSlot)) {

                    // Do not insert into a slot that cannot be inserted into normally.
                    if (!(slot instanceof MachineSlot machineSlot) || machineSlot.canQuickInsertStack()) {

                        ItemStack itemstack = slot.getItem();
                        if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack)) {
                            int j = itemstack.getCount() + stack.getCount();
                            int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());
                            if (j <= maxSize) {
                                stack.setCount(0);
                                itemstack.setCount(j);
                                slot.setChanged();
                                flag = true;
                            } else if (itemstack.getCount() < maxSize) {
                                stack.shrink(maxSize - itemstack.getCount());
                                itemstack.setCount(maxSize);
                                slot.setChanged();
                                flag = true;
                            }
                        }
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while(true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                if (!(slot1 instanceof GhostMachineSlot)) {
                    ItemStack itemstack1 = slot1.getItem();
                    if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
                        if (stack.getCount() > slot1.getMaxStackSize()) {
                            slot1.set(stack.split(slot1.getMaxStackSize()));
                        } else {
                            slot1.set(stack.split(stack.getCount()));
                        }

                        slot1.setChanged();
                        flag = true;
                        break;
                    }
                }

                if (reverseDirection) {
                    --i;
                } else {
                    ++i;
                }
            }
        }

        return flag;
    }
}
