package com.enderio.base.common.menu;

import com.enderio.base.common.capability.FluidFilterCapability;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.network.FilterUpdatePacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class FluidFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final FluidFilterCapability capability;

    public FluidFilterMenu(MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack stack) {
        super(pMenuType, pContainerId);
        this.stack = stack;

        var resourceFilter = stack.getCapability(EIOCapabilities.Filter.ITEM);
        if (!(resourceFilter instanceof FluidFilterCapability filterCapability)) {
            throw new IllegalArgumentException();
        }

        capability = filterCapability;

        for (int i = 0; i < capability.size(); i++) {
            int pSlot = i;
            addSlot(new FluidFilterSlot(fluidStack -> capability.setEntry(pSlot, fluidStack) ,i ,14 + ( i % 5) * 18, 35 + 20 * ( i / 5)));
        }
        addInventorySlots(14,119, inventory);
    }

    public FluidFilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        this(EIOMenus.FLUID_FILTER.get(), pContainerId, inventory, stack);
    }

    public static FluidFilterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new FluidFilterMenu(EIOMenus.FLUID_FILTER.get(), pContainerId, inventory, inventory.player.getMainHandItem());
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return pPlayer.getMainHandItem().equals(stack);
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

    public FluidFilterCapability getFilter() {
        return capability;
    }

    public void setNbt(Boolean nbt) {
        PacketDistributor.sendToServer(new FilterUpdatePacket(nbt, capability.isInvert()));
        capability.setNbt(nbt);
    }

    public void setInverted(Boolean inverted) {
        PacketDistributor.sendToServer(new FilterUpdatePacket(capability.isNbt(), inverted));
        capability.setInverted(inverted);
    }

    @Override
    public void clicked(int pSlotId, int pButton, ClickType pClickType, Player pPlayer) {
        if (pSlotId > 0 && pSlotId < capability.size()) {
            if (!capability.getEntry(pSlotId).isEmpty()) {
                capability.setEntry(pSlotId, FluidStack.EMPTY);
            }
        }
        super.clicked(pSlotId, pButton, pClickType, pPlayer);
    }
}
