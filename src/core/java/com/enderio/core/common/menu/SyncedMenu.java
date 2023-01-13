package com.enderio.core.common.menu;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.core.common.network.SyncClientToServerMenuPacket;
import com.enderio.core.common.sync.EnderDataSlot;
import com.enderio.core.common.sync.SyncMode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class SyncedMenu<T extends EnderBlockEntity> extends AbstractContainerMenu {

    @Nullable private final T blockEntity;
    private final Inventory inventory;

    private final List<EnderDataSlot<?>> clientToServerSlots = new ArrayList<>();
    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private boolean playerSlotsHidden = false;
    protected SyncedMenu(@Nullable T blockEntity, Inventory inventory, @Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
        this.blockEntity = blockEntity;
        this.inventory = inventory;
        if (blockEntity != null) {
            clientToServerSlots.addAll(blockEntity.getClientDecidingDataSlots());
        }
    }

    protected void addClientDecidingDataSlot(EnderDataSlot<?> dataSlot) {
        clientToServerSlots.add(dataSlot);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        sync(false);
    }

    @Override
    public void broadcastFullState() {
        super.broadcastFullState();
        sync(true);
    }

    private void sync(boolean fullSync) {
        if (inventory.player instanceof ServerPlayer player && blockEntity != null) {
            blockEntity.sendPacket(player, blockEntity.createUpdatePacket(fullSync, SyncMode.GUI));
        }
    }

    public void clientTick() {
        ListTag listNBT = new ListTag();
        for (int i = 0; i < clientToServerSlots.size(); i++) {
            Optional<CompoundTag> optionalNBT = clientToServerSlots.get(i).toOptionalNBT();

            if (optionalNBT.isPresent()) {
                CompoundTag elementNBT = optionalNBT.get();
                elementNBT.putInt("dataSlotIndex", i);
                listNBT.add(elementNBT);
            }
        }
        if (!listNBT.isEmpty()) {
            CoreNetwork.sendToServer(new SyncClientToServerMenuPacket(containerId, listNBT));
        }
    }

    @Nullable
    public T getBlockEntity() {
        return blockEntity;
    }

    public List<EnderDataSlot<?>> getClientToServerSlots() {
        return clientToServerSlots;
    }
    
    public void addInventorySlots(int xPos, int yPos) {

        // Hotbar
        for (int x = 0; x < 9; x++) {
            Slot ref = new Slot(inventory, x, xPos + x * 18, yPos + 58);
            playerInventorySlots.add(ref);
            this.addSlot(ref);
        }

        // Inventory
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                Slot ref = new Slot(inventory, x + y * 9 + 9, xPos + x * 18, yPos + y * 18);
                playerInventorySlots.add(ref);
                this.addSlot(ref);
            }
        }

    }

    public boolean arePlayerSlotsHidden() {
        return playerSlotsHidden;
    }

    public void hidePlayerSlots(boolean shouldHide) {
        if (playerSlotsHidden == shouldHide) {
            return;
        }
        playerSlotsHidden = shouldHide;
        int offset = playerSlotsHidden ? 1000 : -1000;
        for (int i = 0; i < 36; i++) {
            playerInventorySlots.get(i).y += offset;
        }
    }
}
