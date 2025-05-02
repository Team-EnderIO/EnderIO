package com.enderio.base.common.menu;

import com.enderio.base.common.filter.SimpleItemStackFilter;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.core.common.network.menu.BoolSyncSlot;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SimpleItemFilterMenu extends AbstractFilterMenu {

    public static int IS_INVERTED_BUTTON_ID = 1;
    public static int SHOULD_COMPARE_COMPONENTS_BUTTON_ID = 2;

    public static final int BASIC_FILTER_SIZE = 5;
    public static final int ADVANCED_FILTER_SIZE = 10;

    public static SimpleItemFilterMenu basic(int containerId, Inventory playerInventory, FilterAccess filterAccess) {
        return new SimpleItemFilterMenu(EIOMenus.BASIC_ITEM_FILTER.get(), BASIC_FILTER_SIZE, containerId, playerInventory, filterAccess);
    }

    public static SimpleItemFilterMenu basic(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        return new SimpleItemFilterMenu(EIOMenus.BASIC_ITEM_FILTER.get(), BASIC_FILTER_SIZE, containerId, playerInventory);
    }

    public static SimpleItemFilterMenu advanced(int containerId, Inventory playerInventory, FilterAccess filterAccess) {
        return new SimpleItemFilterMenu(EIOMenus.ADVANCED_ITEM_FILTER.get(), ADVANCED_FILTER_SIZE, containerId, playerInventory, filterAccess);
    }

    public static SimpleItemFilterMenu advanced(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        return new SimpleItemFilterMenu(EIOMenus.ADVANCED_ITEM_FILTER.get(), ADVANCED_FILTER_SIZE, containerId, playerInventory);
    }

    public final int slotCount;

    @Nullable
    private final NonNullList<ItemStack> clientItems;

    private final BoolSyncSlot isInvertedSyncSlot;
    private final BoolSyncSlot shouldCompareComponentsSyncSlot;

    protected SimpleItemFilterMenu(@Nullable MenuType<?> menuType, int slotCount, int containerId, Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.slotCount = slotCount;
        this.clientItems = null;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(() -> {
            var filterStack = getFilterStack();
            var filter = filterStack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);
            return filter.isInverted();
        }));

        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(() -> {
            var filterStack = getFilterStack();
            var filter = filterStack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);
            return filter.shouldCompareComponents();
        }));

        for (int i = 0; i < slotCount; i++) {
            final int slotIndex = i;
            addSlot(new ItemFilterSlot(() -> getItemInFilter(slotIndex), stack -> setItemInFilter(slotIndex, stack),
                i, 14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 119);
    }

    protected SimpleItemFilterMenu(@Nullable MenuType<?> menuType, int slotCount, int containerId, Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.slotCount = slotCount;
        this.clientItems = NonNullList.withSize(slotCount, ItemStack.EMPTY);

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.standalone());
        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.standalone());

        for (int i = 0; i < slotCount; i++) {
            final int slotIndex = i;
            addSlot(new ItemFilterSlot(() -> clientItems.get(slotIndex), stack -> clientItems.set(slotIndex, stack),
                i, 14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 119);
    }

    public boolean isInverted() {
        return isInvertedSyncSlot.get();
    }

    public boolean shouldCompareComponents() {
        return shouldCompareComponentsSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private ItemStack getItemInFilter(int slotIndex) {
        var filterStack = getFilterStack();
        var filter = filterStack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);

        if (slotIndex >= filter.matches().size()) {
            return ItemStack.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setItemInFilter(int slotIndex, ItemStack stack) {
        var filterStack = getFilterStack();
        var filter = filterStack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);

        var matches = NonNullList.withSize(slotCount, ItemStack.EMPTY);

        for (int i = 0; i < matches.size(); i++) {
            matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : ItemStack.EMPTY);
        }

        matches.set(slotIndex, stack);

        filterStack.set(EIODataComponents.SIMPLE_ITEM_STACK_FILTER,
            new SimpleItemStackFilter(matches, filter.shouldCompareComponents(), filter.isInverted()));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == IS_INVERTED_BUTTON_ID) {
            var filterStack = getFilterStack();
            var filter = filterStack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);
            filterStack.set(EIODataComponents.SIMPLE_ITEM_STACK_FILTER,
                new SimpleItemStackFilter(filter.matches(), filter.shouldCompareComponents(), !filter.isInverted()));
            return true;
        } else if (id == SHOULD_COMPARE_COMPONENTS_BUTTON_ID) {
            var filterStack = getFilterStack();
            var filter = filterStack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, SimpleItemStackFilter.EMPTY);
            filterStack.set(EIODataComponents.SIMPLE_ITEM_STACK_FILTER,
                new SimpleItemStackFilter(filter.matches(), !filter.shouldCompareComponents(), filter.isInverted()));
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
