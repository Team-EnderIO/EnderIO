package com.enderio.conduits.common.menu;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.network.TimerFilterPacket;
import com.enderio.conduits.common.redstone.RedstoneTimerFilter;
import com.enderio.core.common.network.CoreNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public class RedstoneTimerFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneTimerFilter filter;

    protected RedstoneTimerFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;

        LazyOptional<ResourceFilter> resourceFilter = stack.getCapability(EIOCapabilities.FILTER);

        filter = resourceFilter.map(filter -> {
            if (filter instanceof RedstoneTimerFilter cap) {
                return cap;
            }
            throw new IllegalArgumentException();

        }).orElseThrow(IllegalArgumentException::new);

        addInventorySlots(14,119, inventory);
    }

    protected RedstoneTimerFilterMenu(int pContainerId, Inventory inventory, ItemStack pStack) {
        this(ConduitMenus.REDSTONE_TIMER_FILTER.get(), pContainerId, inventory, pStack);
    }


    public static RedstoneTimerFilterMenu factory(@Nullable MenuType<RedstoneTimerFilterMenu> pMenuType, int i, Inventory inventory, FriendlyByteBuf byteBuf) {
        return new RedstoneTimerFilterMenu(i, inventory, inventory.player.getMainHandItem());
    }

    public RedstoneTimerFilter getFilter() {
        return filter;
    }

    public void setTimer(String timer) {
        try {
            filter.setMaxTicks(Integer.parseInt(timer));
            CoreNetwork.sendToServer(new TimerFilterPacket(filter.getTicks(), filter.getMaxTicks()));
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

    public void addInventorySlots(int xPos, int yPos, Inventory inventory) {

        // Hotbar
        for (int x = 0; x < 9; x++) {
            Slot ref = new Slot(inventory, x, xPos + x * 18, yPos + 58);
            this.addSlot(ref);
        }

        // Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot ref = new Slot(inventory, x + y * 9 + 9, xPos + x * 18, yPos + y * 18);
                this.addSlot(ref);
            }
        }

    }
}
