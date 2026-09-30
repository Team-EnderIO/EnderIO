package com.enderio.conduits.common.menu;

import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.network.FilterUpdatePacket;
import com.enderio.conduits.common.integrations.mekanism.ChemicalFilterCapability;
import com.enderio.conduits.common.integrations.mekanism.ChemicalFilterSlot;
import com.enderio.conduits.common.integrations.mekanism.MekanismIntegration;
import com.enderio.core.common.network.CoreNetwork;
import mekanism.api.chemical.merged.BoxedChemicalStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ChemicalFilterMenu extends AbstractContainerMenu {

    private final ItemStack stack;
    private final ChemicalFilterCapability capability;

    public ChemicalFilterMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory inventory, ItemStack stack) {
        super(pMenuType, pContainerId);
        this.stack = stack;

        capability = stack.getCapability(EIOCapabilities.FILTER)
            .filter(ChemicalFilterCapability.class::isInstance)
            .map(ChemicalFilterCapability.class::cast)
            .orElseThrow(IllegalArgumentException::new);

        for (int i = 0; i < capability.size(); i++) {
            int pSlot = i;
            addSlot(new ChemicalFilterSlot(chemicalStack -> capability.setEntry(pSlot, chemicalStack), i, 14 + (i % 5) * 18, 35 + 20 * (i / 5)));
        }
        addInventorySlots(14, 119, inventory);
    }

    public ChemicalFilterMenu(int pContainerId, Inventory inventory, ItemStack stack) {
        this(MekanismIntegration.CHEMICAL_FILTER_MENU.get(), pContainerId, inventory, stack);
    }

    public static ChemicalFilterMenu factory(@Nullable MenuType<ChemicalFilterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        return new ChemicalFilterMenu(MekanismIntegration.CHEMICAL_FILTER_MENU.get(), pContainerId, inventory, inventory.player.getMainHandItem());
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

        for (int x = 0; x < 9; x++) {
            Slot ref = new Slot(inventory, x, xPos + x * 18, yPos + 58);
            this.addSlot(ref);
        }

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
        CoreNetwork.sendToServer(new FilterUpdatePacket(false, inverted));
        capability.setInverted(inverted);
    }

    @Override
    public void doClick(int slotId, int button, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < capability.size()) {
            if (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE) {
                return;
            }

            if (!capability.getEntry(slotId).isEmpty()) {
                capability.setEntry(slotId, BoxedChemicalStack.EMPTY);
            }
        }

        super.doClick(slotId, button, clickType, player);
    }
}