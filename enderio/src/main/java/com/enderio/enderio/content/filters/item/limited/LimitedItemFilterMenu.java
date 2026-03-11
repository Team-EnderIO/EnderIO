package com.enderio.enderio.content.filters.item.limited;

import com.enderio.core.common.network.menu.BoolSyncSlot;
import com.enderio.core.common.network.menu.EnumSyncSlot;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.item.general.DamageFilterMode;
import com.enderio.enderio.init.EIODataComponents;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class LimitedItemFilterMenu extends AbstractFilterMenu<LimitedItemFilter> {

    public static final int SHOULD_COMPARE_COMPONENTS_BUTTON_ID = 2;

    @Nullable
    private final NonNullList<ItemStack> clientItems;

    private final BoolSyncSlot shouldCompareComponentsSyncSlot;
    private final EnumSyncSlot<DamageFilterMode> damageFilterSyncSlot;

    public LimitedItemFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory,
            FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.clientItems = null;

        this.shouldCompareComponentsSyncSlot = addSyncSlot(
                BoolSyncSlot.readOnly(() -> getFilter().shouldCompareComponents()));

        this.damageFilterSyncSlot = addUpdatableSyncSlot(EnumSyncSlot.simple(DamageFilterMode.class,
                () -> getFilterStack().getOrDefault(EIODataComponents.LIMITED_ITEM_FILTER, LimitedItemFilter.EMPTY)
                        .damageFilterMode(),
                (mode) -> modifyFilterStack(stack -> {
                    var filter = stack.getOrDefault(EIODataComponents.LIMITED_ITEM_FILTER, LimitedItemFilter.EMPTY);
                    stack.set(EIODataComponents.LIMITED_ITEM_FILTER,
                            new LimitedItemFilter(filter.matches(), filter.shouldCompareComponents(), mode));
                    return stack;
                })));

        for (int i = 0; i < LimitedItemFilter.SLOT_COUNT; i++) {
            final int slotIndex = i;
            addSlot(new LimitedItemFilterSlot(() -> getItemInFilter(slotIndex),
                    stack -> setItemInFilter(slotIndex, stack), i, 14 + (i % 9) * 18, 27 + 18 * (i / 9)));
        }

        addPlayerInventorySlots(14, 45 + 2 * 18);
    }

    public LimitedItemFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.clientItems = NonNullList.withSize(LimitedItemFilter.SLOT_COUNT, ItemStack.EMPTY);

        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.standalone());

        this.damageFilterSyncSlot = addUpdatableSyncSlot(EnumSyncSlot.standalone(DamageFilterMode.class));

        for (int i = 0; i < LimitedItemFilter.SLOT_COUNT; i++) {
            final int slotIndex = i;
            addSlot(new LimitedItemFilterSlot(() -> clientItems.get(slotIndex),
                    stack -> clientItems.set(slotIndex, stack), i, 14 + (i % 9) * 18, 27 + 18 * (i / 9)));
        }

        addPlayerInventorySlots(14, 45 + 2 * 18);
    }

    @Override
    protected DataComponentType<LimitedItemFilter> dataComponentType() {
        return EIODataComponents.LIMITED_ITEM_FILTER;
    }

    @Override
    protected LimitedItemFilter defaultFilter() {
        return LimitedItemFilter.EMPTY;
    }

    public boolean shouldCompareComponents() {
        return shouldCompareComponentsSyncSlot.get();
    }

    public DamageFilterMode damageFilterMode() {
        return damageFilterSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public void setDamageFilterMode(DamageFilterMode mode) {
        damageFilterSyncSlot.set(mode);
        updateSlot(damageFilterSyncSlot);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private ItemStack getItemInFilter(int slotIndex) {
        var filter = getFilter();
        if (slotIndex >= filter.matches().size()) {
            return ItemStack.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setItemInFilter(int slotIndex, ItemStack stack) {
        modifyFilter(filter -> {
            var matches = NonNullList.withSize(LimitedItemFilter.SLOT_COUNT, ItemStack.EMPTY);
            for (int i = 0; i < matches.size(); i++) {
                matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : ItemStack.EMPTY);
            }

            matches.set(slotIndex, stack);

            return new LimitedItemFilter(matches, filter.shouldCompareComponents(), filter.damageFilterMode());
        });
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < slots.size() && getSlot(slotId) instanceof LimitedItemFilterSlot limitedSlot) {
            if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) {
                return;
            }

            if (!limitedSlot.isEmpty()) {
                ItemStack cursor = getCarried();
                ItemStack current = limitedSlot.getResource();
                // If cursor holds the same item, increase the stored limit by the cursor count.
                if (!cursor.isEmpty() && ItemStack.isSameItem(cursor, current)) {
                    limitedSlot.setResource(current.copyWithCount(current.getCount() + cursor.getCount()));
                    return;
                }
            }
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == SHOULD_COMPARE_COMPONENTS_BUTTON_ID) {
            modifyFilter(filter -> new LimitedItemFilter(filter.matches(), !filter.shouldCompareComponents(),
                    filter.damageFilterMode()));
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
