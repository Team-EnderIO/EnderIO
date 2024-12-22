package com.enderio.machines.common.blocks.wired_charger;

import com.enderio.core.common.network.menu.FloatSyncSlot;
import com.enderio.machines.common.blocks.base.menu.MachineSlot;
import com.enderio.machines.common.blocks.base.menu.PoweredMachineMenu;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

public class WiredChargerMenu extends PoweredMachineMenu<WiredChargerBlockEntity> {

    private final FloatSyncSlot chargeProgressSlot;

    public WiredChargerMenu(int pContainerId, Inventory inventory, WiredChargerBlockEntity blockEntity) {
        super(MachineMenus.WIRED_CHARGER.get(), pContainerId, inventory, blockEntity);
        addSlots();

        chargeProgressSlot = addSyncSlot(FloatSyncSlot.readOnly(blockEntity::getChargeProgress));
    }

    public WiredChargerMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(MachineMenus.WIRED_CHARGER.get(), MachineBlockEntities.WIRED_CHARGER.get(), containerId, playerInventory,
                buf);
        addSlots();

        chargeProgressSlot = addSyncSlot(FloatSyncSlot.standalone());
    }

    private void addSlots() {
        addCapacitorSlot(33, 60);
        addSlot(new MachineSlot(getMachineInventory(), WiredChargerBlockEntity.ITEM_TO_CHARGE, 75, 28));
        addSlot(new MachineSlot(getMachineInventory(), WiredChargerBlockEntity.ITEM_CHARGED, 126, 28));

        addPlayerInventorySlots(29, 84);
        addArmorSlots(6, 12);

        // Add offhand slot
        addSlot(new Slot(getPlayerInventory(), 40, 6, 84).setBackground(InventoryMenu.BLOCK_ATLAS,
                InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));
    }

    public float getChargeProgress() {
        return chargeProgressSlot.get();
    }
}
