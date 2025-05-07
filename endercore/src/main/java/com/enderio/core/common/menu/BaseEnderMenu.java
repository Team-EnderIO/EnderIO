package com.enderio.core.common.menu;

import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket;
import com.enderio.core.common.network.menu.ClientboundSyncSlotDataPacket.PayloadPair;
import com.enderio.core.common.network.menu.ContainerSyncData;
import com.enderio.core.common.network.menu.ServerboundSetSyncSlotDataPacket;
import com.enderio.core.common.network.menu.SyncSlot;
import com.enderio.core.common.network.menu.SyncSlot.ChangeType;
import com.enderio.core.common.network.menu.payload.SlotPayload;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

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
                addSlot(createPlayerInventorySlot(getPlayerInventory(), x + y * 9 + 9, xStart + x * 18,
                        yStart + y * 18));
            }
        }
    }

    protected void addPlayerHotbarSlots(int x, int y) {
        for (int i = 0; i < 9; i++) {
            addSlot(createPlayerInventorySlot(getPlayerInventory(), i, x + i * 18, y));
        }
    }

    protected void addArmorSlots(int x, int y) {
        for (int i = 0; i < EQUIPMENT_SLOTS.length; i++) {
            addSlot(createPlayerArmorSlot(getPlayerInventory(), 36 + (3 - i), x, y + i * 18, EQUIPMENT_SLOTS[i]));
        }
    }

    protected Slot createPlayerInventorySlot(Inventory inventory, int slot, int x, int y) {
        return new Slot(inventory, slot, x, y);
    }

    protected ArmorSlot createPlayerArmorSlot(Inventory inventory, int slot, int x, int y, EquipmentSlot slotType) {
        return new ArmorSlot(inventory, slot, x, y, slotType);
    }

    // endregion

    // region Enhanced Data Sync

    private final List<SyncSlot> syncSlots = new ArrayList<>();
    private final List<SyncSlot> clientUpdateSyncSlots = new ArrayList<>();

    /**
     * Add a sync slot which is only synced server -> client.
     * @apiNote Order matters! Ensure client & server menus add the slots in the same order!
     * @param syncSlot The slot to add.
     * @return The added slot.
     */
    protected <T extends SyncSlot> T addSyncSlot(T syncSlot) {
        syncSlots.add(syncSlot);
        return syncSlot;
    }

    /**
     * Add a sync slot which can accept updates from the client.
     * @param syncSlot The slot to add.
     * @return The added slot.
     */
    protected <T extends SyncSlot> T addUpdatableSyncSlot(T syncSlot) {
        syncSlots.add(syncSlot);
        clientUpdateSyncSlots.add(syncSlot);
        return syncSlot;
    }

    protected <T extends ContainerSyncData> T addContainerSyncData(T syncData) {
        syncSlots.addAll(syncData.syncSlots());
        clientUpdateSyncSlots.addAll(syncData.updatableSyncSlots());
        return syncData;
    }

    /**
     * Call this to send any updates to this slot to the server.
     */
    protected void updateSlot(SyncSlot syncSlot) {
        if (!clientUpdateSyncSlots.contains(syncSlot)) {
            throw new IllegalArgumentException("This slot is not client updatable!");
        }

        if (playerInventory.player instanceof LocalPlayer) {
            short slotIndex = (short) clientUpdateSyncSlots.indexOf(syncSlot);
            ChangeType changeType = syncSlot.detectChanges();

            if (changeType != ChangeType.NONE) {
                var payload = syncSlot.createPayload(playerInventory.player.level(), ChangeType.FULL);
                PacketDistributor.sendToServer(new ServerboundSetSyncSlotDataPacket(containerId, slotIndex, payload));
            }
        }
    }

    /**
     * Handle a slot payload from the server.
     * @param slotIndex Slot to update.
     * @param payload Data payload.
     */
    public void clientHandleIncomingPayload(short slotIndex, SlotPayload payload) {
        if (slotIndex >= 0 && slotIndex < syncSlots.size()) {
            var slot = syncSlots.get(slotIndex);
            slot.unpackPayload(playerInventory.player.level(), payload);
        } else {
            // TODO: Log this error.
        }
    }

    /**
     * Handle a slot payload from the client.
     * @param slotIndex Slot to uodate.
     * @param payload Data payload.
     */
    public void serverHandleIncomingPayload(short slotIndex, SlotPayload payload) {
        if (slotIndex >= 0 && slotIndex < clientUpdateSyncSlots.size()) {
            var slot = clientUpdateSyncSlots.get(slotIndex);
            slot.unpackPayload(playerInventory.player.level(), payload);
        } else {
            // TODO: Log this error.
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (playerInventory.player instanceof ServerPlayer player) {
            List<PayloadPair> payloads = new ArrayList<>();
            Level level = player.level();

            for (short i = 0; i < syncSlots.size(); i++) {
                var slot = syncSlots.get(i);
                ChangeType changeType = slot.detectChanges();
                if (changeType != ChangeType.NONE) {
                    var payload = slot.createPayload(level, ChangeType.FULL);
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
            Level level = player.level();

            for (short i = 0; i < syncSlots.size(); i++) {
                var slot = syncSlots.get(i);

                // Initialize the change detectors, we're sending all data no matter what.
                slot.detectChanges();

                var payload = slot.createPayload(level, ChangeType.FULL);
                payloads.add(new PayloadPair(i, payload));
            }

            if (!payloads.isEmpty()) {
                PacketDistributor.sendToPlayer(player, new ClientboundSyncSlotDataPacket(containerId, payloads));
            }
        }
    }

    // endregion

}
