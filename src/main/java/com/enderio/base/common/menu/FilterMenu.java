package com.enderio.base.common.menu;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.network.FilterUpdatePacket;
import com.enderio.base.common.network.RequestTravelPacket;
import com.enderio.core.common.capability.ItemFilterCapability;
import com.enderio.core.common.network.NetworkUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final ItemFilterCapability capability;

    public FilterMenu(MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack stack) {
        super(pMenuType, pContainerId);
        this.stack = stack;
        this.capability = stack.getCapability(EIOCapabilities.Filter.ITEM);
        NonNullList<ItemStack> items = capability.getItems();
        for (int i = 0; i < items.size(); i++) {
            addSlot(new FilterSlot(items, i, 13 + ( i % 5) * 18, 43 + 10 * ( i / 5)));
        }
        addInventorySlots(8,84, inventory);
    }

    public FilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        this(EIOMenus.ITEM_FILTER.get(), pContainerId, inventory, stack);
    }

    public static FilterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new FilterMenu(EIOMenus.ITEM_FILTER.get(), pContainerId, inventory, inventory.player.getMainHandItem());
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getMainHandItem().equals(stack);
    }

    public void addInventorySlots(int xPos, int yPos, Inventory inventory) {

        // Hotbar
        for (int x = 0; x < 9; x++) {
            Slot ref = new Slot(inventory, x, xPos + x * 18, yPos + 58);
            this.addSlot(ref);
        }

        // Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot ref = new Slot(inventory, x + y * 9 + 9, xPos + x * 18, yPos + y * 18);
                this.addSlot(ref);
            }
        }

    }

    public ItemFilterCapability getFilter() {
        return capability;
    }

    public void setNbt(Boolean nbt) {
        NetworkUtil.sendToServer(new FilterUpdatePacket(nbt, capability.isInvert()));
        capability.setNbt(nbt);
    }

    public void setInverted(Boolean inverted) {
        NetworkUtil.sendToServer(new FilterUpdatePacket(capability.isNbt(), inverted));
        capability.setInverted(inverted);
    }
}
