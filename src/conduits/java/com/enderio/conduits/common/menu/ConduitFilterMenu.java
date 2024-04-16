package com.enderio.conduits.common.menu;

import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.items.FilterItem;
import com.enderio.conduits.common.items.FilterInventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * based of <a href="https://github.com/latvian-dev/item-filters/blob/1.18/main/common/src/main/java/dev/latvian/mods/itemfilters/item/InventoryFilterItem.java">InventoryFilterItem</a>
 * <br>
 * this is modified to use capabilities.
 */
public class ConduitFilterMenu extends AbstractContainerMenu {

    public final InteractionHand hand;
    public final FilterInventory inventory;
    public final List<Vector2i> filterSlots;

    protected ConduitFilterMenu(@Nullable MenuType<ConduitFilterMenu> menuType, int containerId , Inventory playerInventory, InteractionHand hand) {
        super(menuType, containerId);
        this.hand = hand;
        this.inventory = new FilterInventory(playerInventory.player.getItemInHand(hand));
        filterSlots = new ArrayList<>();
        this.inventory.getDrawableSlots(filterSlots);

        // 0-n
        for (int i = 0; i < filterSlots.size(); i++) {
            var slotPos = filterSlots.get(i);
            addSlot(new Slot(inventory, i, slotPos.x(), slotPos.y()){
                @Override
                public void onTake(Player player, ItemStack stack) {
                }

                @Override
                public ItemStack remove(int amount) {
                    return ItemStack.EMPTY;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // n-n+27
        int offset = 3 + 9 + 3 + (Math.abs(filterSlots.size()/9) * 18) + 3 + 9 + 8; // same formula as ConduitFilterScreen but with 7 from the texture. java compile surely flattens the constant math
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(playerInventory, x + y * 9 + 9,  23 + (x * 18), offset + (y * 18)));
            }
        }

        // n+27-n+36
        for (int x = 0; x < 9; x++) {
            final int i = x;
            addSlot(new Slot(playerInventory, x, 23 + x * 18, offset+4+54) {
                @Override
                public boolean mayPickup(Player player) {
                    return i != player.getInventory().selected;
                }
            });
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index >= filterSlots.size() && inventory.getFilterCap().getItems().size() < filterSlots.size() && index != player.getInventory().selected + filterSlots.size()) {
            Slot slot = getSlot(index);

            if (slot != null) {
                ItemStack stack = slot.getItem();

                if (!stack.isEmpty()) {
                    for (ItemStack stack1 : inventory.getFilterCap().getItems()) {
                        if (stack1.getItem() == stack.getItem() && ItemStack.isSameItemSameTags(stack1, stack)) {
                            return ItemStack.EMPTY;
                        }
                    }

                    ItemStack is = stack.copy();
                    is.setCount(1);
                    inventory.getFilterCap().getItems().add(is);
                    inventory.getFilterCap().save();
                    return stack;
                }
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < filterSlots.size()) {
            ItemStack stack = this.getCarried().copy();
            stack.setCount(1);

            if (stack.isEmpty()) {
                if (slotId < inventory.getFilterCap().getItems().size()) {
                    inventory.getFilterCap().getItems().remove(slotId);
                    inventory.getFilterCap().save();
                }
            } else {
                for (ItemStack stack1 : inventory.getFilterCap().getItems()) {
                    if (stack1.getItem() == stack.getItem() && ItemStack.isSameItemSameTags(stack1, stack)) {
                        return;
                    }
                }

                if (slotId < inventory.getFilterCap().getItems().size()) {
                    inventory.getFilterCap().getItems().set(slotId, stack);
                    inventory.getFilterCap().save();
                } else {
                    inventory.getFilterCap().getItems().add(stack);
                    inventory.getFilterCap().save();
                }
            }
        } else {
            super.clicked(slotId, dragType, clickType, player);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public static ConduitFilterMenu factory(@Nullable MenuType<ConduitFilterMenu> pMenuType, int pContainerId,
            Inventory inventory, @Nullable FriendlyByteBuf buf) {
        return new ConduitFilterMenu(pMenuType, pContainerId, inventory, buf == null ? InteractionHand.MAIN_HAND : buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }
}
