package com.enderio.modconduits.mods.mekanism;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.network.FilterUpdatePacket;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChemicalFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final ChemicalFilterCapability capability;

    public ChemicalFilterMenu(MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack stack) {
        super(pMenuType, pContainerId);
        this.stack = stack;

        var resourceFilter = stack.getCapability(EIOCapabilities.Filter.ITEM);
        if (!(resourceFilter instanceof ChemicalFilterCapability filterCapability)) {
            throw new IllegalArgumentException();
        }

        capability = filterCapability;

        for (int i = 0; i < capability.size(); i++) {
            int pSlot = i;
            addSlot(new ChemicalFilterSlot(fluidStack -> capability.setEntry(pSlot, fluidStack) ,i ,14 + ( i % 5) * 18, 35 + 20 * ( i / 5)));
        }
        addInventorySlots(14,119, inventory);
    }

    public ChemicalFilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        this(MekanismModule.CHEMICAL_FILTER_MENU.get(), pContainerId, inventory, stack);
    }

    public static ChemicalFilterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new ChemicalFilterMenu(MekanismModule.CHEMICAL_FILTER_MENU.get(), pContainerId, inventory, inventory.player.getMainHandItem());
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

    public ChemicalFilterCapability getFilter() {
        return capability;
    }

    public void setInverted(Boolean inverted) {
        PacketDistributor.sendToServer(new FilterUpdatePacket(capability.isNbt(), inverted));
        capability.setInverted(inverted);
    }

    @Override
    public void doClick(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < capability.size()) {
            if (clickType == ClickType.PICKUP) {
                if (!capability.getEntry(slotId).isEmpty()) {
                    capability.setEntry(slotId, ChemicalStack.EMPTY);
                }
            } else if (clickType == ClickType.SWAP) {
                return;
            }
        }

        super.doClick(slotId, button, clickType, player);
    }
}
