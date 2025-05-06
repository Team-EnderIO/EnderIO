package com.enderio.base.common.filter.item;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.item.filter.EnderItemFilterItem;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.core.common.network.menu.BoolSyncSlot;
import com.enderio.core.common.network.menu.EnumSyncSlot;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class EnderItemFilterMenu extends AbstractFilterMenu {

    public static int IS_INVERTED_BUTTON_ID = 1;
    public static int SHOULD_COMPARE_COMPONENTS_BUTTON_ID = 2;

    public final EnderItemFilterItem.Type type;

    @Nullable
    private final NonNullList<ItemStack> clientItems;

    private final BoolSyncSlot isInvertedSyncSlot;
    private final BoolSyncSlot shouldCompareComponentsSyncSlot;

    @Nullable
    private final EnumSyncSlot<DamageFilterMode> damageFilterSyncSlot;

    public EnderItemFilterMenu(@Nullable MenuType<?> menuType, EnderItemFilterItem.Type type, int containerId,
            Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.type = type;
        this.clientItems = null;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(
                () -> getFilterStack().getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY)
                        .isDenyList()));

        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(
                () -> getFilterStack().getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY)
                        .shouldCompareComponents()));

        if (this.type.canFilterByDamage()) {
            this.damageFilterSyncSlot = addUpdatableSyncSlot(EnumSyncSlot.simple(DamageFilterMode.class,
                    () -> getFilterStack().getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY)
                            .damageFilterMode(),
                    (mode) -> modifyFilterStack(stack -> {
                        var filter = stack.getOrDefault(EIODataComponents.ITEM_FILTER,
                                EnderItemFilter.EMPTY);
                        stack.set(EIODataComponents.ITEM_FILTER, new EnderItemFilter(filter.matches(),
                                filter.isDenyList(), filter.shouldCompareComponents(), mode));
                        return stack;
                    })));
        } else {
            this.damageFilterSyncSlot = null;
        }

        for (int i = 0; i < this.type.slotCount(); i++) {
            final int slotIndex = i;
            addSlot(new ItemFilterSlot(() -> getItemInFilter(slotIndex), stack -> setItemInFilter(slotIndex, stack), i,
                    14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 47 + type.rowCount() * 18);
    }

    public EnderItemFilterMenu(@Nullable MenuType<?> menuType, EnderItemFilterItem.Type type, int containerId,
            Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.type = type;
        this.clientItems = NonNullList.withSize(this.type.slotCount(), ItemStack.EMPTY);

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.standalone());
        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.standalone());

        if (this.type.canFilterByDamage()) {
            this.damageFilterSyncSlot = addUpdatableSyncSlot(EnumSyncSlot.standalone(DamageFilterMode.class));
        } else {
            this.damageFilterSyncSlot = null;
        }

        for (int i = 0; i < this.type.slotCount(); i++) {
            final int slotIndex = i;
            addSlot(new ItemFilterSlot(() -> clientItems.get(slotIndex), stack -> clientItems.set(slotIndex, stack), i,
                    14 + (i % 9) * 18, 35 + 18 * (i / 9)));
        }

        addPlayerInventorySlots(14, 47 + type.rowCount() * 18);
    }

    public boolean isInverted() {
        return isInvertedSyncSlot.get();
    }

    public boolean shouldCompareComponents() {
        return shouldCompareComponentsSyncSlot.get();
    }

    public DamageFilterMode damageFilterMode() {
        if (damageFilterSyncSlot == null) {
            return DamageFilterMode.IGNORE;
        }

        return damageFilterSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public void setDamageFilterMode(DamageFilterMode mode) {
        if (damageFilterSyncSlot != null) {
            damageFilterSyncSlot.set(mode);
            updateSlot(damageFilterSyncSlot);
        }
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private ItemStack getItemInFilter(int slotIndex) {
        var filter = getFilterStack().getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY);
        if (slotIndex >= filter.matches().size()) {
            return ItemStack.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setItemInFilter(int slotIndex, ItemStack stack) {
        modifyFilterStack(filterStack -> {
            var filter = filterStack.getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY);

            // Copy match list
            var matches = NonNullList.withSize(type.slotCount(), ItemStack.EMPTY);
            for (int i = 0; i < matches.size(); i++) {
                matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : ItemStack.EMPTY);
            }

            // Change the entry
            matches.set(slotIndex, stack);

            // Set the new filter
            filterStack.set(EIODataComponents.ITEM_FILTER, new EnderItemFilter(matches, filter.isDenyList(),
                    filter.shouldCompareComponents(), filter.damageFilterMode()));
            return filterStack;
        });
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == IS_INVERTED_BUTTON_ID) {
            modifyFilterStack(stack -> {
                var filter = stack.getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY);
                stack.set(EIODataComponents.ITEM_FILTER, new EnderItemFilter(filter.matches(),
                        !filter.isDenyList(), filter.shouldCompareComponents(), filter.damageFilterMode()));
                return stack;
            });

            return true;
        } else if (id == SHOULD_COMPARE_COMPONENTS_BUTTON_ID && type.canMatchComponents()) {
            modifyFilterStack(stack -> {
                var filter = stack.getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY);
                stack.set(EIODataComponents.ITEM_FILTER, new EnderItemFilter(filter.matches(),
                        filter.isDenyList(), !filter.shouldCompareComponents(), filter.damageFilterMode()));
                return stack;
            });
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public void doClick(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < type.slotCount()) {
            // Only allow PICKUP (click) or QUICK_MOVE (shift + click) events.
            if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) {
                return;
            }

//            if (!capability.getEntry(slotId).isEmpty()) {
//                capability.setEntry(slotId, ItemStack.EMPTY);
//            }
        }

        super.doClick(slotId, button, clickType, player);
    }
}
