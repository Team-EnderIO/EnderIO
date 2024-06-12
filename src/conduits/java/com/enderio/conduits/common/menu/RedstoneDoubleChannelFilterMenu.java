package com.enderio.conduits.common.menu;

import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.network.DoubleChannelPacket;
import com.enderio.conduits.common.redstone.DoubleRedstoneChannel;
import com.enderio.conduits.common.redstone.RedstoneInsertFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class RedstoneDoubleChannelFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneInsertFilter filter;
    private final DoubleRedstoneChannel channels;

    protected RedstoneDoubleChannelFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;
        var resourceFilter = pStack.getCapability(EIOCapabilities.Filter.ITEM);
        if (!(resourceFilter instanceof RedstoneInsertFilter filter)) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
        if (!(resourceFilter instanceof DoubleRedstoneChannel channels)) {
            throw new IllegalArgumentException();
        }
        this.channels = channels;
    }

    protected RedstoneDoubleChannelFilterMenu(int pContainerId, Inventory inventory, ItemStack pStack) {
        this(ConduitMenus.REDSTONE_DOUBLE_CHANNEL_FILTER.get(), pContainerId, inventory, pStack);
    }


    public static RedstoneDoubleChannelFilterMenu factory(int i, Inventory inventory, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        return new RedstoneDoubleChannelFilterMenu(i, inventory, inventory.player.getMainHandItem());
    }

    public RedstoneInsertFilter getFilter() {
        return filter;
    }

    public DoubleRedstoneChannel getChannels() {
        return channels;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getItemInHand(InteractionHand.MAIN_HAND).equals(stack);
    }

    public void setFirstChannel(ColorControl colorControl) {
        channels.setFirstChannel(colorControl);
        PacketDistributor.sendToServer(new DoubleChannelPacket(channels.getFirstChannel(), channels.getSecondChannel()));
    }

    public void setSecondChannel(ColorControl colorControl) {
        channels.setSecondChannel(colorControl);
        PacketDistributor.sendToServer(new DoubleChannelPacket(channels.getFirstChannel(), channels.getSecondChannel()));
    }
}
