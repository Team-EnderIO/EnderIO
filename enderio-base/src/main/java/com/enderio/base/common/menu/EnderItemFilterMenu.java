package com.enderio.base.common.menu;

import com.enderio.base.common.filter.DamageFilterMode;
import com.enderio.base.common.filter.EnderItemStackFilter;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.item.filter.EnderItemFilterItem;
import com.enderio.core.common.network.menu.BoolSyncSlot;
import com.enderio.core.common.network.menu.EnumSyncSlot;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.IContainerFactory;
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

    public EnderItemFilterMenu(@Nullable MenuType<?> menuType, EnderItemFilterItem.Type type, int containerId, Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.type = type;
        this.clientItems = null;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(() -> {
            var filterStack = getFilterStack();
            var filter = filterStack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);
            return filter.isDenyList();
        }));

        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(() -> {
            var filterStack = getFilterStack();
            var filter = filterStack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);
            return filter.shouldCompareComponents();
        }));

        if (this.type.canFilterByDamage()) {
            this.damageFilterSyncSlot = addUpdatableSyncSlot(EnumSyncSlot.simple(DamageFilterMode.class, () -> {
                var filterStack = getFilterStack();
                var filter = filterStack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);
                return filter.damageFilterMode();
            }, (mode) -> {
                var filterStack = getFilterStack();
                var filter = filterStack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);
                filterStack.set(EIODataComponents.ITEM_STACK_FILTER,
                        new EnderItemStackFilter(filter.matches(), filter.isDenyList(),
                                filter.shouldCompareComponents(), mode));
            }));
        } else {
            this.damageFilterSyncSlot = null;
        }

        for (int i = 0; i < this.type.slotCount(); i++) {
            final int slotIndex = i;
            addSlot(new ItemFilterSlot(() -> getItemInFilter(slotIndex), stack -> setItemInFilter(slotIndex, stack),
                i, 14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 119);
    }

    public EnderItemFilterMenu(@Nullable MenuType<?> menuType, EnderItemFilterItem.Type type, int containerId, Inventory playerInventory) {
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

    public DamageFilterMode damageFilterMode() {
        if (damageFilterSyncSlot == null) {
            return DamageFilterMode.IGNORE;
        }

        return damageFilterSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    public void setDamageFilterMode(DamageFilterMode mode) {
        damageFilterSyncSlot.set(mode);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private ItemStack getItemInFilter(int slotIndex) {
        var filterStack = getFilterStack();
        var filter = filterStack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);

        if (slotIndex >= filter.matches().size()) {
            return ItemStack.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setItemInFilter(int slotIndex, ItemStack stack) {
        var filterStack = getFilterStack();
        var filter = filterStack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);

        var matches = NonNullList.withSize(type.slotCount(), ItemStack.EMPTY);

        for (int i = 0; i < matches.size(); i++) {
            matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : ItemStack.EMPTY);
        }

        matches.set(slotIndex, stack);

        filterStack.set(EIODataComponents.ITEM_STACK_FILTER,
            new EnderItemStackFilter(matches, filter.isDenyList(), filter.shouldCompareComponents(), filter.damageFilterMode()));
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == IS_INVERTED_BUTTON_ID) {
            var filterStack = getFilterStack();
            var filter = filterStack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);
            filterStack.set(EIODataComponents.ITEM_STACK_FILTER,
                new EnderItemStackFilter(filter.matches(), !filter.isDenyList(), filter.shouldCompareComponents(), filter.damageFilterMode()));
            return true;
        } else if (id == SHOULD_COMPARE_COMPONENTS_BUTTON_ID && type.canMatchComponents()) {
            var filterStack = getFilterStack();
            var filter = filterStack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);
            filterStack.set(EIODataComponents.ITEM_STACK_FILTER,
                new EnderItemStackFilter(filter.matches(), filter.isDenyList(), !filter.shouldCompareComponents(), filter.damageFilterMode()));
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
