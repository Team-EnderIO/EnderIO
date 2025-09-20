package com.enderio.base.common.filter.fluid;

import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.core.common.network.menu.BoolSyncSlot;
import com.enderio.core.common.network.menu.FluidStackSyncSlot;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class EnderFluidFilterMenu extends AbstractFilterMenu<EnderFluidFilter> {

    public static final int IS_INVERTED_BUTTON_ID = 1;
    public static final int SHOULD_COMPARE_COMPONENTS_BUTTON_ID = 2;

    public final EnderFluidFilterItem.Type type;

    private final BoolSyncSlot isInvertedSyncSlot;
    private final BoolSyncSlot shouldCompareComponentsSyncSlot;

    public EnderFluidFilterMenu(@Nullable MenuType<?> menuType, EnderFluidFilterItem.Type type, int containerId,
            Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.type = type;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(() -> getFilter().isDenyList()));
        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(() -> getFilter().shouldCompareComponents()));

        for (int i = 0; i < this.type.slotCount(); i++) {
            final int slotIndex = i;

            // Add sync slot for the fluid slot
            addSyncSlot(FluidStackSyncSlot.readOnly(() -> getFluidInFilter(slotIndex)));

            addSlot(new FluidFilterSlot(() -> getFluidInFilter(slotIndex), stack -> setFluidInFilter(slotIndex, stack),
                    i, 14 + (i % 5) * 18, 27 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 45 + type.rowCount() * 18);
    }

    public EnderFluidFilterMenu(@Nullable MenuType<?> menuType, EnderFluidFilterItem.Type type, int containerId,
            Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.type = type;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.standalone());
        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.standalone());

        for (int i = 0; i < this.type.slotCount(); i++) {
            final var syncSlot = addSyncSlot(FluidStackSyncSlot.standalone());
            addSlot(new FluidFilterSlot(syncSlot::get, syncSlot::set, i, 14 + (i % 9) * 18, 27 + 18 * (i / 9)));
        }

        addPlayerInventorySlots(14, 45 + type.rowCount() * 18);
    }

    @Override
    protected Supplier<DataComponentType<EnderFluidFilter>> dataComponentType() {
        return EIODataComponents.FLUID_FILTER;
    }

    @Override
    protected EnderFluidFilter defaultFilter() {
        return EnderFluidFilter.EMPTY;
    }

    public boolean isInverted() {
        return isInvertedSyncSlot.get();
    }

    public boolean shouldCompareComponents() {
        return shouldCompareComponentsSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private FluidStack getFluidInFilter(int slotIndex) {
        var filter = getFilter();
        if (slotIndex >= filter.matches().size()) {
            return FluidStack.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setFluidInFilter(int slotIndex, FluidStack stack) {
        modifyFilter(filter -> {
            // Copy match list
            var matches = NonNullList.withSize(type.slotCount(), FluidStack.EMPTY);
            for (int i = 0; i < matches.size(); i++) {
                matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : FluidStack.EMPTY);
            }

            // Change the entry
            matches.set(slotIndex, stack);

            // Set the new filter
            return new EnderFluidFilter(matches, filter.isDenyList(), filter.shouldCompareComponents());
        });
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == IS_INVERTED_BUTTON_ID) {
            modifyFilter(filter -> new EnderFluidFilter(filter.matches(), !filter.isDenyList(), filter.shouldCompareComponents()));
            return true;
        } else if (id == SHOULD_COMPARE_COMPONENTS_BUTTON_ID && type.canMatchComponents()) {
            modifyFilter(filter -> new EnderFluidFilter(filter.matches(), filter.isDenyList(), !filter.shouldCompareComponents()));
            return true;
        }

        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }
}
