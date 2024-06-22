package com.enderio.conduits.common.menu;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.api.misc.ColorControl;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.network.CountFilterPacket;
import com.enderio.conduits.common.redstone.RedstoneCountFilter;
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

public class RedstoneCountFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneCountFilter filter;

    public RedstoneCountFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;

        LazyOptional<ResourceFilter> resourceFilter = stack.getCapability(EIOCapabilities.FILTER);

        filter = resourceFilter.map(filter -> {
            if (filter instanceof RedstoneCountFilter cap) {
                return cap;
            }
            throw new IllegalArgumentException();

        }).orElseThrow(IllegalArgumentException::new);

        addInventorySlots(14,119, inventory);
    }

    public RedstoneCountFilterMenu(int pContainerId, Inventory inventory, ItemStack pStack) {
        this(ConduitMenus.REDSTONE_COUNT_FILTER.get(), pContainerId, inventory, pStack);
    }

    public static RedstoneCountFilterMenu factory(@Nullable MenuType<RedstoneCountFilterMenu> pMenuType, int i, Inventory inventory, FriendlyByteBuf byteBuf) {
        return new RedstoneCountFilterMenu(i, inventory, inventory.player.getMainHandItem());
    }


    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getItemInHand(InteractionHand.MAIN_HAND).equals(stack);
    }

    public RedstoneCountFilter getFilter() {
        return filter;
    }

    public void setCount(String maxCount) {
        try {
            filter.setMaxCount(Integer.parseInt(maxCount));
            CoreNetwork.sendToServer(new CountFilterPacket(filter.getChannel(), filter.getMaxCount(), filter.getCount(), filter.isDeactivated()));
        } catch (Exception e) {

        }
    }

    public void setChannel(ColorControl channel) {
        filter.setChannel(channel);
        CoreNetwork.sendToServer(new CountFilterPacket(filter.getChannel(), filter.getMaxCount(), filter.getCount(), filter.isDeactivated()));
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
