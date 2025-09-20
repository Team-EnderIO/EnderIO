package com.enderio.modconduits.common.modules.mekanism.chemical_filter;

import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.filter.fluid.EnderFluidFilterItem;
import com.enderio.core.common.network.menu.BoolSyncSlot;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import me.liliandev.ensure.ensures.EnsureSide;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EnderChemicalFilterMenu extends AbstractFilterMenu<EnderChemicalFilter> {

    public static final int IS_INVERTED_BUTTON_ID = 1;

    public final EnderChemicalFilterItem.Type type;

    private final BoolSyncSlot isInvertedSyncSlot;

    public EnderChemicalFilterMenu(@Nullable MenuType<?> menuType, EnderChemicalFilterItem.Type type, int containerId,
        Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.type = type;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot
            .readOnly(() -> getFilter().isDenyList()));

        for (int i = 0; i < this.type.slotCount(); i++) {
            final int slotIndex = i;

            // Add sync slot for the chemical slot
            addSyncSlot(ChemicalStackSyncSlot.readOnly(() -> getChemicalInFilter(slotIndex)));

            addSlot(new ChemicalFilterSlot(() -> getChemicalInFilter(slotIndex), stack -> setChemicalInFilter(slotIndex, stack),
                i, 14 + (i % 5) * 18, 27 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 45 + type.rowCount() * 18);
    }

    public EnderChemicalFilterMenu(@Nullable MenuType<?> menuType, EnderChemicalFilterItem.Type type, int containerId,
        Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.type = type;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.standalone());

        for (int i = 0; i < this.type.slotCount(); i++) {
            final var syncSlot = addSyncSlot(ChemicalStackSyncSlot.standalone());
            addSlot(new ChemicalFilterSlot(syncSlot::get, syncSlot::set, i, 14 + (i % 9) * 18, 27 + 18 * (i / 9)));
        }

        addPlayerInventorySlots(14, 45 + type.rowCount() * 18);
    }

    @Override
    protected Supplier<DataComponentType<EnderChemicalFilter>> dataComponentType() {
        return MekanismModule.CHEMICAL_FILTER;
    }

    @Override
    protected EnderChemicalFilter defaultFilter() {
        return EnderChemicalFilter.EMPTY;
    }

    public boolean isInverted() {
        return isInvertedSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private ChemicalStack getChemicalInFilter(int slotIndex) {
        var filter = getFilter();
        if (slotIndex >= filter.matches().size()) {
            return ChemicalStack.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setChemicalInFilter(int slotIndex, ChemicalStack stack) {
        modifyFilter(filter -> {
            // Copy match list
            var matches = NonNullList.withSize(type.slotCount(), ChemicalStack.EMPTY);
            for (int i = 0; i < matches.size(); i++) {
                matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : ChemicalStack.EMPTY);
            }

            // Change the entry
            matches.set(slotIndex, stack);

            // Set the new filter
            return new EnderChemicalFilter(matches, filter.isDenyList());
        });
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == IS_INVERTED_BUTTON_ID) {
            modifyFilter(filter -> new EnderChemicalFilter(filter.matches(), !filter.isDenyList()));
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}

