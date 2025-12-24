package com.enderio.enderio.content.filters.redstone;

import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.foundation.network.packets.ServerboundTimerFilterPacket;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jetbrains.annotations.Nullable;

public class RedstoneTimerFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final RedstoneTimerFilter filter;

    protected RedstoneTimerFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack pStack) {
        super(pMenuType, pContainerId);
        this.stack = pStack;
        var resourceFilter = pStack.getCapability(EnderIOCapabilities.REDSTONE_EXTRACT_FILTER);
        if (!(resourceFilter instanceof RedstoneTimerFilter filter)) {
            throw new IllegalArgumentException();
        }
        this.filter = filter;
        addInventorySlots(14,119, inventory);
    }

    protected RedstoneTimerFilterMenu(int pContainerId, Inventory inventory, ItemStack pStack) {
        this(EIOMenus.REDSTONE_TIMER_FILTER.get(), pContainerId, inventory, pStack);
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
            ClientPacketDistributor.sendToServer(new ServerboundTimerFilterPacket(filter.getTicks(), filter.getMaxTicks()));
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
