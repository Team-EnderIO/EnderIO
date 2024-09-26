package com.enderio.base.common.menu;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.network.FilterUpdatePacket;
import com.enderio.core.common.capability.FluidFilterCapability;
import com.enderio.core.common.menu.FluidFilterSlot;
import com.enderio.core.common.network.CoreNetwork;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.List;

public class FluidFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final FluidFilterCapability capability;

    public FluidFilterMenu(MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack stack) {
        super(pMenuType, pContainerId);
        this.stack = stack;

        LazyOptional<ResourceFilter> resourceFilter = stack.getCapability(EIOCapabilities.FILTER);

        capability = resourceFilter.map(filter -> {
            if (filter instanceof FluidFilterCapability cap) {
                return cap;
            }
            throw new IllegalArgumentException();

        }).orElseThrow(IllegalArgumentException::new);

        for (int i = 0; i < capability.size(); i++) {
            int pSlot = i;
            addSlot(new FluidFilterSlot(fluidStack -> capability.setEntry(pSlot, fluidStack) ,i ,14 + ( i % 5) * 18, 35 + 20 * ( i / 5)));
        }
        addInventorySlots(14,119, inventory);
    }

    public FluidFilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        this(EIOMenus.FLUID_FILTER.get(), pContainerId, inventory, stack);
    }

    public static FluidFilterMenu factory(@Nullable MenuType<FluidFilterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
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
        CoreNetwork.sendToServer(new FilterUpdatePacket(nbt, capability.isInvert()));
        capability.setNbt(nbt);
    }

    public void setInverted(Boolean inverted) {
        CoreNetwork.sendToServer(new FilterUpdatePacket(capability.isNbt(), inverted));
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
