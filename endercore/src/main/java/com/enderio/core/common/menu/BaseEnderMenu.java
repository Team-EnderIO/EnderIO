package com.enderio.core.common.menu;

import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket;
import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket.PayloadPair;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import com.enderio.core.common.network.menu.SyncSlot;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEnderMenu extends AbstractContainerMenu {

    private final Inventory playerInventory;

    protected static final int PLAYER_INVENTORY_SIZE = 36;

    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD,
            EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };

    protected BaseEnderMenu(@Nullable MenuType<?> menuType, int containerId, Inventory playerInventory) {
        super(menuType, containerId);
        this.playerInventory = playerInventory;
    }

    // region Inventory Utilities

    protected Inventory getPlayerInventory() {
        return playerInventory;
    }

    protected void addPlayerInventorySlots(int x, int y) {
        addPlayerMainInventorySlots(x, y);
        addPlayerHotbarSlots(x, y + 58);
    }

    protected void addPlayerMainInventorySlots(int xStart, int yStart) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                addSlot(new Slot(getPlayerInventory(), x + y * 9 + 9, xStart + x * 18, yStart + y * 18));
            }
        }
    }

    protected void addPlayerHotbarSlots(int x, int y) {
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(getPlayerInventory(), i, x + i * 18, y));
        }
    }

    protected void addArmorSlots(int x, int y) {
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            addSlot(new ArmorSlot(getPlayerInventory(), 36 + (3 - i), x, y + i * 18, EQUIPMENT_SLOTS[i]));
        }
    }

    // endregion

    // region Enhanced Data Sync

    private List<SyncSlot> syncSlots = new ArrayList<>();

    protected <T extends SyncSlot> T addSyncSlot(T syncSlot) {
        syncSlots.add(syncSlot);
        return syncSlot;
    }

    public void handleIncomingPayload(short slotIndex, SlotPayload payload) {
        if (slotIndex >= 0 && slotIndex < syncSlots.size()) {
            var slot = syncSlots.get(slotIndex);
            slot.acceptPayload(payload);
        } else {
            // TODO: Log this error.
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (playerInventory.player instanceof ServerPlayer player) {
            List<PayloadPair> payloads = new ArrayList<>();
            RegistryAccess registryAccess = player.registryAccess();

            for (short i = 0; i < syncSlots.size(); i++) {
                var slot = syncSlots.get(i);
                SyncSlot.ChangeType changeType = slot.detectChanges();
                if (changeType != SyncSlot.ChangeType.NONE) {
                    var payload = slot.getPayload(registryAccess, SyncSlot.ChangeType.FULL);
                    payloads.add(new PayloadPair(i, payload));
                }
            }

            if (!payloads.isEmpty()) {
                PacketDistributor.sendToPlayer(player, new ClientboundSyncSlotDataPacket(containerId, payloads));
            }
        }
    }

    @Override
    public void sendAllDataToRemote() {
        super.sendAllDataToRemote();

        if (playerInventory.player instanceof ServerPlayer player) {
            List<PayloadPair> payloads = new ArrayList<>();
            RegistryAccess registryAccess = player.registryAccess();

            for (short i = 0; i < syncSlots.size(); i++) {
                var slot = syncSlots.get(i);

                // Initialize the change detectors, we're sending all data no matter what.
                slot.detectChanges();

                var payload = slot.getPayload(registryAccess, SyncSlot.ChangeType.FULL);
                payloads.add(new PayloadPair(i, payload));
            }

            if (!payloads.isEmpty()) {
                PacketDistributor.sendToPlayer(player, new ClientboundSyncSlotDataPacket(containerId, payloads));
            }
        }
    }

    // endregion
}
