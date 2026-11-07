package com.enderio.enderio.content.filters.redstone;

import com.enderio.core.common.network.menu.IntSyncSlot;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class RedstoneTimerFilterMenu extends AbstractFilterMenu<RedstoneTimerFilter.Component> {

    private final IntSyncSlot maxTicks;

    public RedstoneTimerFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, FilterAccess filterAccess) {
        super(menuType, containerId, inventory, filterAccess);

        maxTicks = addUpdatableSyncSlot(IntSyncSlot.simple(() -> getFilter().maxTicks(), value -> {
            if (value > 0) {
                modifyFilter(filter -> new RedstoneTimerFilter.Component(0, value));
            }
        }));

        addPlayerInventorySlots(14, 119);
    }

    private RedstoneTimerFilterMenu(int containerId, Inventory inventory, ItemStack stack) {
        super(EIOMenus.REDSTONE_TIMER_FILTER.get(), containerId, inventory);
        var initial = stack.getOrDefault(EIODataComponents.REDSTONE_TIMER_FILTER, RedstoneTimerFilter.INSTANCE);

        maxTicks = addUpdatableSyncSlot(IntSyncSlot.standalone());
        maxTicks.set(initial.maxTicks());
        maxTicks.detectChanges();

        addPlayerInventorySlots(14, 119);
    }

    public static RedstoneTimerFilterMenu factory(int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        return new RedstoneTimerFilterMenu(containerId, inventory, ItemStack.STREAM_CODEC.decode(buffer));
    }

    @Override
    protected DataComponentType<RedstoneTimerFilter.Component> dataComponentType() {
        return EIODataComponents.REDSTONE_TIMER_FILTER;
    }

    @Override
    protected RedstoneTimerFilter.Component defaultFilter() {
        return RedstoneTimerFilter.INSTANCE;
    }

    public int getMaxTicks() {
        return maxTicks.get();
    }

    public void setTimer(String input) {
        try {
            int value = Integer.parseInt(input);
            if (value > 0) {
                maxTicks.set(value);
                updateSlot(maxTicks);
            }
        } catch (NumberFormatException ignored) {
            // Leave the filter unchanged until a valid number is confirmed.
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
