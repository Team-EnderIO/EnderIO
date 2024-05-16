package com.enderio.base.common.menu;

import com.enderio.base.common.init.EIOMenus;
import com.enderio.core.common.capability.FluidFilterCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class FluidFilterMenu extends FilterMenu {

    public FluidFilterMenu(MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack stack) {
        super(pMenuType, pContainerId, inventory, stack);
    }

    public FluidFilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        super(EIOMenus.FLUID_FILTER.get(), pContainerId, inventory, stack);
    }

    public static FluidFilterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new FluidFilterMenu(EIOMenus.FLUID_FILTER.get(), pContainerId, inventory, inventory.player.getMainHandItem());
    }

    @Override
    public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        if (getFilter() instanceof FluidFilterCapability filterCapability) {
            if (pSlotId < filterCapability.getEntries().size()) {
                if (!filterCapability.getEntries().get(pSlotId).isEmpty()) {
                    filterCapability.setEntry(pSlotId, FluidStack.EMPTY);
                }
            }
            super.clicked(pSlotId, pButton, pClickType, pPlayer);
        }
    }
}
