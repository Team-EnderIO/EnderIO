package com.enderio.base.common.filter.fluid;

import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.filter.item.EnderItemStackFilter;
import com.enderio.base.common.filter.item.ItemFilterSlot;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.item.filter.EnderFluidStackFilterItem;
import com.enderio.base.common.menu.FluidFilterSlot;
import com.enderio.core.common.network.menu.BoolSyncSlot;
import com.enderio.core.common.network.menu.FluidStackSyncSlot;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EnderFluidStackFilterMenu extends AbstractFilterMenu {

    public static int IS_INVERTED_BUTTON_ID = 1;
    public static int SHOULD_COMPARE_COMPONENTS_BUTTON_ID = 2;

    public final EnderFluidStackFilterItem.Type type;

    @Nullable
    private final NonNullList<ItemStack> clientItems;

    private final BoolSyncSlot isInvertedSyncSlot;
    private final BoolSyncSlot shouldCompareComponentsSyncSlot;

    public EnderFluidStackFilterMenu(@Nullable MenuType<?> menuType, EnderFluidStackFilterItem.Type type, int containerId,
                                     Inventory playerInventory, FilterAccess filterAccess) {
        super(menuType, containerId, playerInventory, filterAccess);
        this.type = type;
        this.clientItems = null;

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(
                () -> getFilterStack().getOrDefault(EIODataComponents.FLUID_STACK_FILTER, EnderFluidStackFilter.EMPTY)
                        .isDenyList()));

        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.readOnly(
                () -> getFilterStack().getOrDefault(EIODataComponents.FLUID_STACK_FILTER, EnderFluidStackFilter.EMPTY)
                        .shouldCompareComponents()));

        for (int i = 0; i < this.type.slotCount(); i++) {
            final int slotIndex = i;

            // Add sync slot for the fluid slot
            addSyncSlot(FluidStackSyncSlot.readOnly(() -> getFluidInFilter(slotIndex)));

            addSlot(new FluidFilterSlot(() -> getFluidInFilter(slotIndex), stack -> setFluidInFilter(slotIndex, stack), i,
                    14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }

        addPlayerInventorySlots(14, 47 + type.rowCount() * 18);
    }

    public EnderFluidStackFilterMenu(@Nullable MenuType<?> menuType, EnderFluidStackFilterItem.Type type, int containerId,
                                     Inventory playerInventory) {
        super(menuType, containerId, playerInventory);
        this.type = type;
        this.clientItems = NonNullList.withSize(this.type.slotCount(), ItemStack.EMPTY);

        this.isInvertedSyncSlot = addSyncSlot(BoolSyncSlot.standalone());
        this.shouldCompareComponentsSyncSlot = addSyncSlot(BoolSyncSlot.standalone());

        for (int i = 0; i < this.type.slotCount(); i++) {
            final var syncSlot = addSyncSlot(FluidStackSyncSlot.standalone());
            addSlot(new FluidFilterSlot(syncSlot::get, syncSlot::set, i, 14 + (i % 9) * 18, 35 + 18 * (i / 9)));
        }

        addPlayerInventorySlots(14, 47 + type.rowCount() * 18);
    }

    public boolean isInverted() {
        return isInvertedSyncSlot.get();
    }

    public boolean shouldCompareComponents() {
        return shouldCompareComponentsSyncSlot.get();
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private FluidStack getFluidInFilter(int slotIndex) {
        var filter = getFilterStack().getOrDefault(EIODataComponents.FLUID_STACK_FILTER, EnderFluidStackFilter.EMPTY);
        if (slotIndex >= filter.matches().size()) {
            return FluidStack.EMPTY;
        }

        return filter.matches().get(slotIndex);
    }

    @EnsureSide(EnsureSide.Side.SERVER)
    private void setFluidInFilter(int slotIndex, FluidStack stack) {
        modifyFilterStack(filterStack -> {
            var filter = filterStack.getOrDefault(EIODataComponents.FLUID_STACK_FILTER, EnderFluidStackFilter.EMPTY);

            // Copy match list
            var matches = NonNullList.withSize(type.slotCount(), FluidStack.EMPTY);
            for (int i = 0; i < matches.size(); i++) {
                matches.set(i, i < filter.matches().size() ? filter.matches().get(i) : FluidStack.EMPTY);
            }

            // Change the entry
            matches.set(slotIndex, stack);

            // Set the new filter
            filterStack.set(EIODataComponents.FLUID_STACK_FILTER, new EnderFluidStackFilter(matches, filter.isDenyList(),
                    filter.shouldCompareComponents()));
            return filterStack;
        });
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == IS_INVERTED_BUTTON_ID) {
            modifyFilterStack(stack -> {
                var filter = stack.getOrDefault(EIODataComponents.FLUID_STACK_FILTER, EnderFluidStackFilter.EMPTY);
                stack.set(EIODataComponents.FLUID_STACK_FILTER, new EnderFluidStackFilter(filter.matches(),
                        !filter.isDenyList(), filter.shouldCompareComponents()));
                return stack;
            });

            return true;
        } else if (id == SHOULD_COMPARE_COMPONENTS_BUTTON_ID && type.canMatchComponents()) {
            modifyFilterStack(stack -> {
                var filter = stack.getOrDefault(EIODataComponents.FLUID_STACK_FILTER, EnderFluidStackFilter.EMPTY);
                stack.set(EIODataComponents.FLUID_STACK_FILTER, new EnderFluidStackFilter(filter.matches(),
                        filter.isDenyList(), !filter.shouldCompareComponents()));
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
