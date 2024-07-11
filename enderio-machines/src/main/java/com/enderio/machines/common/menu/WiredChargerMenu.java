package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.WiredChargerBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.menu.base.PoweredMachineMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class WiredChargerMenu extends PoweredMachineMenu<WiredChargerBlockEntity> {

    public WiredChargerMenu(@Nullable WiredChargerBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(MachineMenus.WIRED_CHARGER.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            addSlot(new MachineSlot(getMachineInventory(), getCapacitorSlotIndex(), 33, 60));
            addSlot(new MachineSlot(getMachineInventory(), WiredChargerBlockEntity.ITEM_TO_CHARGE, 75, 28));
            addSlot(new MachineSlot(getMachineInventory(), WiredChargerBlockEntity.ITEM_CHARGED, 126, 28));
        }

        addPlayerInventorySlots(29,84);
        addArmorSlots(6,12);

        //Add offhand slot
        addSlot(new Slot(inventory, 40, 6, 84)
            .setBackground(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD));
    }

    public float getProgress() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getProgress();
    }

    public static WiredChargerMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());

        if (entity instanceof WiredChargerBlockEntity castBlockEntity) {
            return new WiredChargerMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new WiredChargerMenu(null, inventory, pContainerId);
    }
}
