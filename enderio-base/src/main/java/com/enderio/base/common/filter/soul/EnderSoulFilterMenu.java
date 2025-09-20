package com.enderio.base.common.filter.soul;

import com.enderio.base.api.soul.Soul;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.soul.StoredEntityDataSyncSlot;
import com.enderio.core.common.network.menu.BoolSyncSlot;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EnderSoulFilterMenu extends AbstractFilterMenu<EnderSoulFilter> {

    public static final int IS_INVERTED_BUTTON_ID = 1;
    public static final int SHOULD_COMPARE_TAGS_BUTTON_ID = 2;

    public final EnderSoulFilterItem.Type type;

    private final BoolSyncSlot isInvertedSyncSlot;
    private final BoolSyncSlot shouldCompareTagsSyncSlot;

    public EnderSoulFilterMenu(@Nullable MenuType<?> menuType, EnderSoulFilterItem.Type type, int containerId,
                               Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.type = type;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(() -> getFilter().isDenyList()));
        this.shouldCompareTagsSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(() -> getFilter().shouldCompareTags()));

        for (int i = 0; i < this.type.slotCount(); i++) {
            final int slotIndex = i;

            // Add sync slot for the fluid slot
            addSyncSlot(StoredEntityDataSyncSlot.readOnly(() -> getEntityInFilter(slotIndex)));

            addSlot(new SoulFilterSlot(() -> getEntityInFilter(slotIndex), stack -> setEntityInFilter(slotIndex, stack),
                    i, 14 + (i % 5) * 18, 27 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 45 + type.rowCount() * 18);
    }

    public EnderSoulFilterMenu(@Nullable MenuType<?> menuType, EnderSoulFilterItem.Type type, int containerId,
                               Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.type = type;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.standalone());
        this.shouldCompareTagsSyncSlot = addSyncSlot(BoolSyncSlot.standalone());

        for (int i = 0; i < this.type.slotCount(); i++) {
            final var syncSlot = addSyncSlot(StoredEntityDataSyncSlot.standalone());
            addSlot(new SoulFilterSlot(syncSlot::get, syncSlot::set, i, 14 + (i % 9) * 18, 27 + 18 * (i / 9)));
        }

        addPlayerInventorySlots(14, 45 + type.rowCount() * 18);
    }

    @Override
    protected Supplier<DataComponentType<EnderSoulFilter>> dataComponentType() {
        return EIODataComponents.SOUL_FILTER;
    }

    @Override
    protected EnderSoulFilter defaultFilter() {
        return EnderSoulFilter.EMPTY;
    }

    public boolean isInverted() {
        return isInvertedSyncSlot.get();
    }

    public boolean shouldCompareTags() {
        return shouldCompareTagsSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private Soul getEntityInFilter(int slotIndex) {
        var filter = getFilter();
        if (slotIndex >= filter.matches().size()) {
            return Soul.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setEntityInFilter(int slotIndex, Soul entity) {
        modifyFilter(filter -> {
            // Copy match list
            var matches = NonNullList.withSize(type.slotCount(), Soul.EMPTY);
            for (int i = 0; i < matches.size(); i++) {
                matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : Soul.EMPTY);
            }

            // Change the entry
            matches.set(slotIndex, entity);

            // Set the new filter
            return new EnderSoulFilter(matches, filter.isDenyList(), filter.shouldCompareTags());
        });
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == IS_INVERTED_BUTTON_ID) {
            modifyFilter(filter -> new EnderSoulFilter(filter.matches(), !filter.isDenyList(), filter.shouldCompareTags()));
            return true;
        } else if (id == SHOULD_COMPARE_TAGS_BUTTON_ID && type.canMatchComponents()) {
            modifyFilter(filter -> new EnderSoulFilter(filter.matches(), filter.isDenyList(), !filter.shouldCompareTags()));
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
