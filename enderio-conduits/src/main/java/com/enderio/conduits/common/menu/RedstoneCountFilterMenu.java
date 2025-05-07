package com.enderio.conduits.common.menu;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.conduits.api.ConduitCapabilities;
import com.enderio.conduits.common.init.ConduitMenus;
import com.enderio.conduits.common.network.CountFilterPacket;
import com.enderio.conduits.common.redstone.RedstoneCountFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class RedstoneCountFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneCountFilter filter;

    public RedstoneCountFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;
        var resourceFilter = pStack.getCapability(ConduitCapabilities.REDSTONE_INSERT_FILTER);
        if (!(resourceFilter instanceof RedstoneCountFilter filter)) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
        addInventorySlots(14,119, inventory);
    }

    public RedstoneCountFilterMenu(int pContainerId, Inventory inventory, ItemStack pStack) {
        this(ConduitMenus.REDSTONE_COUNT_FILTER.get(), pContainerId, inventory, pStack);
    }

    public static RedstoneCountFilterMenu factory(int i, Inventory inventory, RegistryFriendlyByteBuf registryFriendlyByteBuf) {
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
            PacketDistributor.sendToServer(new CountFilterPacket(filter.getChannel(), filter.getMaxCount(), filter.getCount(), filter.isDeactivated()));
        } catch (Exception e) {

        }
    }

    public void setChannel(DyeColor channel) {
        filter.setChannel(channel);
        PacketDistributor.sendToServer(new CountFilterPacket(filter.getChannel(), filter.getMaxCount(), filter.getCount(), filter.isDeactivated()));
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
