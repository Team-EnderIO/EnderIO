package com.enderio.conduits.common.menu;

import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.redstone.RedstoneANDFilter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RedstoneANDFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneANDFilter filter;

    protected RedstoneANDFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;
        var resourceFilter = pStack.getCapability(EIOCapabilities.Filter.ITEM);
        if (!(resourceFilter instanceof RedstoneANDFilter filter)) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
    }

    public RedstoneANDFilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        this(ConduitMenus.REDSTONE_AND_FILTER.get(), pContainerId, inventory, stack);
    }

    public RedstoneANDFilter getFilter() {
        return filter;
    }

    public static RedstoneANDFilterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new RedstoneANDFilterMenu(pContainerId, inventory, inventory.player.getMainHandItem());
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
        filter.setFirstChannel(colorControl, stack);
    }

    public void setSecondChannel(ColorControl colorControl) {
        filter.setSecondChannel(colorControl, stack);
    }
}
