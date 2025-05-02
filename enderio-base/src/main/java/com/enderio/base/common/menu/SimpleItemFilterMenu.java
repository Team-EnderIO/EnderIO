package com.enderio.base.common.menu;

import com.enderio.base.common.filter.SimpleItemStackFilter;
import com.enderio.base.common.init.EIODataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.IContainerFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SimpleItemFilterMenu extends AbstractFilterMenu {

    private final int slotCount;

    private final Container clientInventory;

    public SimpleItemFilterMenu(@Nullable MenuType<?> menuType, int slotCount, int containerId, Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.slotCount = slotCount;
        this.clientInventory = null;

        for (int i = 0; i < slotCount; i++) {
            final int slotIndex = i;
            addSlot(new ItemFilterSlot(() -> getItemInFilter(slotIndex), stack -> setItemInFilter(slotIndex, stack),
                i, 14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 119);
    }

    public SimpleItemFilterMenu(@Nullable MenuType<?> menuType, int slotCount, int containerId, Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.slotCount = slotCount;
        this.clientInventory = new SimpleContainer(slotCount);

        for (int i = 0; i < slotCount; i++) {
            final int slotIndex = i;
            addSlot(new ItemFilterSlot(() -> getItemInFilter(slotIndex), stack -> setItemInFilter(slotIndex, stack),
                i, 14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 119);
    }

    public static IContainerFactory<SimpleItemFilterMenu> clientFactory(MenuType<?> menuType, int slotCount) {
        return (int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) -> new SimpleItemFilterMenu(menuType, slotCount, containerId, playerInventory);
    }

    private ItemStack getItemInFilter(int slotIndex) {
        var filterStack = getFilterStack();
        var filter = filterStack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);

        if (slotIndex >= filter.matches().size()) {
            return ItemStack.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    private void setItemInFilter(int slotIndex, ItemStack stack) {
        var filterStack = getFilterStack();
        var filter = filterStack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);

        List<ItemStack> matches = new ArrayList<>(slotCount);
        matches.addAll(filter.matches());
        matches.set(slotIndex, stack);

        filterStack.set(EIODataComponents.SIMPLE_ITEM_STACK_FILTER,
            new SimpleItemStackFilter(matches, filter.shouldCompareComponents(), filter.isInverted()));
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
