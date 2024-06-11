package com.enderio.conduits.common.menu;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.redstone.RedstoneInsertFilter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class RedstoneInsertFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneInsertFilter filter;

    protected RedstoneInsertFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;
        var resourceFilter = pStack.getCapability(EIOCapabilities.Filter.ITEM);
        if (!(resourceFilter instanceof RedstoneInsertFilter filter)) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
    }

    public RedstoneInsertFilter getFilter() {
        return filter;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getItemInHand(InteractionHand.MAIN_HAND).equals(stack);
    }
}
