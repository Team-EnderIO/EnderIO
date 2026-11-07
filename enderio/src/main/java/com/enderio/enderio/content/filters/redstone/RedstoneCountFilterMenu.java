package com.enderio.enderio.content.filters.redstone;

import com.enderio.core.common.network.menu.EnumSyncSlot;
import com.enderio.core.common.network.menu.IntSyncSlot;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class RedstoneCountFilterMenu extends AbstractFilterMenu<RedstoneCountFilter.Component> {

    private final EnumSyncSlot<DyeColor> channel;
    private final IntSyncSlot maxCount;

    public RedstoneCountFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, FilterAccess filterAccess) {
        super(menuType, containerId, inventory, filterAccess);

        channel = addUpdatableSyncSlot(EnumSyncSlot.simple(DyeColor.class, () -> getFilter().channel1(), value -> modifyFilter(filter ->
                new RedstoneCountFilter.Component(value, filter.maxCount(), filter.count(), filter.deactivated()))));
        maxCount = addUpdatableSyncSlot(IntSyncSlot.simple(() -> getFilter().maxCount(), value -> {
            if (value >= 0) {
                modifyFilter(filter -> new RedstoneCountFilter.Component(filter.channel1(), value, filter.count(), filter.deactivated()));
            }
        }));

        addPlayerInventorySlots(14, 119);
    }

    private RedstoneCountFilterMenu(int containerId, Inventory inventory, ItemStack stack) {
        super(EIOMenus.REDSTONE_COUNT_FILTER.get(), containerId, inventory);
        var initial = stack.getOrDefault(EIODataComponents.REDSTONE_COUNT_FILTER, RedstoneCountFilter.INSTANCE);

        channel = addUpdatableSyncSlot(EnumSyncSlot.standalone(DyeColor.class));
        channel.set(initial.channel1());
        channel.detectChanges();
        maxCount = addUpdatableSyncSlot(IntSyncSlot.standalone());
        maxCount.set(initial.maxCount());
        maxCount.detectChanges();

        addPlayerInventorySlots(14, 119);
    }

    public static RedstoneCountFilterMenu factory(int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        return new RedstoneCountFilterMenu(containerId, inventory, ItemStack.STREAM_CODEC.decode(buffer));
    }

    @Override
    protected DataComponentType<RedstoneCountFilter.Component> dataComponentType() {
        return EIODataComponents.REDSTONE_COUNT_FILTER;
    }

    @Override
    protected RedstoneCountFilter.Component defaultFilter() {
        return RedstoneCountFilter.INSTANCE;
    }

    public DyeColor getChannel() {
        return channel.get();
    }

    public void setChannel(DyeColor value) {
        channel.set(value);
        updateSlot(channel);
    }

    public int getMaxCount() {
        return maxCount.get();
    }

    public void setCount(String input) {
        try {
            int value = Integer.parseInt(input);
            if (value >= 0) {
                maxCount.set(value);
                updateSlot(maxCount);
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
