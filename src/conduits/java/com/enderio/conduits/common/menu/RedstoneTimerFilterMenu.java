package com.enderio.conduits.common.menu;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.network.TimerFilterPacket;
import com.enderio.conduits.common.redstone.RedstoneTimerFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class RedstoneTimerFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneTimerFilter filter;

    protected RedstoneTimerFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;
        var resourceFilter = pStack.getCapability(EIOCapabilities.Filter.ITEM);
        if (!(resourceFilter instanceof RedstoneTimerFilter filter)) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
    }

    protected RedstoneTimerFilterMenu(int pContainerId, Inventory inventory, ItemStack pStack) {
        this(ConduitMenus.REDSTONE_TIMER_FILTER.get(), pContainerId, inventory, pStack);
    }


    public static RedstoneTimerFilterMenu factory(int i, Inventory inventory, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        return new RedstoneTimerFilterMenu(i, inventory, inventory.player.getMainHandItem());
    }

    public RedstoneTimerFilter getFilter() {
        return filter;
    }

    public void setTimer(String timer) {
        try {
            filter.setMaxTicks(Integer.parseInt(timer));
            PacketDistributor.sendToServer(new TimerFilterPacket(filter.getTicks(), filter.getMaxTicks()));
        } catch (Exception e) {

        }
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
