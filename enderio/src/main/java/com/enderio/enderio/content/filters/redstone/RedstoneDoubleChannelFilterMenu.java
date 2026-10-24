package com.enderio.enderio.content.filters.redstone;

import com.enderio.core.common.network.menu.EnumSyncSlot;
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

public class RedstoneDoubleChannelFilterMenu extends AbstractFilterMenu<DoubleRedstoneChannel.Component> {

    private final EnumSyncSlot<DyeColor> firstChannel;
    private final EnumSyncSlot<DyeColor> secondChannel;

    public RedstoneDoubleChannelFilterMenu(@Nullable MenuType<?> menuType, int containerId, Inventory inventory, FilterAccess filterAccess) {
        super(menuType, containerId, inventory, filterAccess);

        firstChannel = addUpdatableSyncSlot(EnumSyncSlot.simple(DyeColor.class, () -> getFilter().channel1(), value -> modifyFilter(filter ->
                new DoubleRedstoneChannel.Component(value, filter.channel2()))));
        secondChannel = addUpdatableSyncSlot(EnumSyncSlot.simple(DyeColor.class, () -> getFilter().channel2(), value -> modifyFilter(filter ->
                new DoubleRedstoneChannel.Component(filter.channel1(), value))));

        addPlayerInventorySlots(14, 119);
    }

    private RedstoneDoubleChannelFilterMenu(int containerId, Inventory inventory, ItemStack stack) {
        super(EIOMenus.REDSTONE_DOUBLE_CHANNEL_FILTER.get(), containerId, inventory);
        var initial = stack.getOrDefault(EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL, DoubleRedstoneChannel.INSTANCE);

        firstChannel = addUpdatableSyncSlot(EnumSyncSlot.standalone(DyeColor.class));
        firstChannel.set(initial.channel1());
        firstChannel.detectChanges();
        secondChannel = addUpdatableSyncSlot(EnumSyncSlot.standalone(DyeColor.class));
        secondChannel.set(initial.channel2());
        secondChannel.detectChanges();

        addPlayerInventorySlots(14, 119);
    }

    public static RedstoneDoubleChannelFilterMenu factory(int containerId, Inventory inventory, RegistryFriendlyByteBuf buffer) {
        return new RedstoneDoubleChannelFilterMenu(containerId, inventory, ItemStack.STREAM_CODEC.decode(buffer));
    }

    @Override
    protected DataComponentType<DoubleRedstoneChannel.Component> dataComponentType() {
        return EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL;
    }

    @Override
    protected DoubleRedstoneChannel.Component defaultFilter() {
        return DoubleRedstoneChannel.INSTANCE;
    }

    public DyeColor getFirstChannel() {
        return firstChannel.get();
    }

    public void setFirstChannel(DyeColor value) {
        firstChannel.set(value);
        updateSlot(firstChannel);
    }

    public DyeColor getSecondChannel() {
        return secondChannel.get();
    }

    public void setSecondChannel(DyeColor value) {
        secondChannel.set(value);
        updateSlot(secondChannel);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
