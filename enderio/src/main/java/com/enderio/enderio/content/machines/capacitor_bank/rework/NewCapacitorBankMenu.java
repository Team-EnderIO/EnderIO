package com.enderio.enderio.content.machines.capacitor_bank.rework;

import com.enderio.core.common.menu.BaseBlockEntityMenu;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NewCapacitorBankMenu extends BaseBlockEntityMenu<NewCapacitorBankBlockEntity> {

    public NewCapacitorBankMenu(int containerId, Inventory playerInventory, NewCapacitorBankBlockEntity blockEntity) {
        super(EIOMenus.NEW_CAPACITOR_BANK.get(), containerId, playerInventory, blockEntity);
        addPlayerInventorySlots(8, 84);
    }

    public NewCapacitorBankMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.NEW_CAPACITOR_BANK.get(), containerId, playerInventory, buf);
        addPlayerInventorySlots(8, 84);
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(getBlockEntity(), player);
    }

    //No inv
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
