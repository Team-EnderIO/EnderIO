package com.enderio.base.common.filter.entity;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.filter.fluid.EnderFluidFilter;
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

public class EnderEntityFilterMenu extends AbstractFilterMenu<EnderEntityFilter> {

    public static int IS_INVERTED_BUTTON_ID = 1;
    public static int SHOULD_COMPARE_TAGS_BUTTON_ID = 2;

    public final EnderEntityFilterItem.Type type;

    private final BoolSyncSlot isInvertedSyncSlot;
    private final BoolSyncSlot shouldCompareComponentsSyncSlot;

    public EnderEntityFilterMenu(@Nullable MenuType<?> menuType, EnderEntityFilterItem.Type type, int containerId,
                                 Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.type = type;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot
                .readOnly(() -> getFilterStack().getOrDefault(EIODataComponents.FLUID_FILTER, EnderFluidFilter.EMPTY)
                        .isDenyList()));

        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot
                .readOnly(() -> getFilterStack().getOrDefault(EIODataComponents.FLUID_FILTER, EnderFluidFilter.EMPTY)
                        .shouldCompareComponents()));

        for (int i = 0; i < this.type.slotCount(); i++) {
            final int slotIndex = i;

            // Add sync slot for the fluid slot
            addSyncSlot(StoredEntityDataSyncSlot.readOnly(() -> getEntityInFilter(slotIndex)));

            addSlot(new EntityFilterSlot(() -> getEntityInFilter(slotIndex), stack -> setEntityInFilter(slotIndex, stack),
                    i, 14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 47 + type.rowCount() * 18);
    }

    public EnderEntityFilterMenu(@Nullable MenuType<?> menuType, EnderEntityFilterItem.Type type, int containerId,
                                 Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.type = type;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.standalone());
        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.standalone());

        for (int i = 0; i < this.type.slotCount(); i++) {
            final var syncSlot = addSyncSlot(StoredEntityDataSyncSlot.standalone());
            addSlot(new EntityFilterSlot(syncSlot::get, syncSlot::set, i, 14 + (i % 9) * 18, 35 + 18 * (i / 9)));
        }

        addPlayerInventorySlots(14, 47 + type.rowCount() * 18);
    }

    @Override
    protected Supplier<DataComponentType<EnderEntityFilter>> dataComponentType() {
        return EIODataComponents.ENTITY_FILTER;
    }

    @Override
    protected EnderEntityFilter defaultFilter() {
        return EnderEntityFilter.EMPTY;
    }

    public boolean isInverted() {
        return isInvertedSyncSlot.get();
    }

    public boolean shouldCompareComponents() {
        return shouldCompareComponentsSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private StoredEntityData getEntityInFilter(int slotIndex) {
        var filter = getFilter();
        if (slotIndex >= filter.matches().size()) {
            return StoredEntityData.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setEntityInFilter(int slotIndex, StoredEntityData entity) {
        modifyFilter(filter -> {
            // Copy match list
            var matches = NonNullList.withSize(type.slotCount(), StoredEntityData.EMPTY);
            for (int i = 0; i < matches.size(); i++) {
                matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : StoredEntityData.EMPTY);
            }

            // Change the entry
            matches.set(slotIndex, entity);

            // Set the new filter
            return new EnderEntityFilter(matches, filter.isDenyList(), filter.shouldCompareTags());
        });
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == IS_INVERTED_BUTTON_ID) {
            modifyFilter(filter -> new EnderEntityFilter(filter.matches(), !filter.isDenyList(), filter.shouldCompareTags()));
            return true;
        } else if (id == SHOULD_COMPARE_TAGS_BUTTON_ID && type.canMatchComponents()) {
            modifyFilter(filter -> new EnderEntityFilter(filter.matches(), filter.isDenyList(), !filter.shouldCompareTags()));
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
