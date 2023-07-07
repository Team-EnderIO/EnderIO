package com.enderio.core.common.menu;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class SyncedMenu<T extends EnderBlockEntity> extends AbstractContainerMenu {

    @Nullable
    private final T blockEntity;
    private final Inventory inventory;

    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private boolean playerInvVisible = true;
    protected SyncedMenu(@Nullable T blockEntity, Inventory inventory, @Nullable MenuType<?> pMenuType, int pContainerId) {
        super(pMenuType, pContainerId);
        this.blockEntity = blockEntity;
        this.inventory = inventory;
    }

    @Nullable
    public T getBlockEntity() {
        return blockEntity;
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

    public boolean getPlayerInvVisible() {
        return playerInvVisible;
    }

    public boolean setPlayerInvVisible(boolean visible) {
        if (playerInvVisible != visible) {
            playerInvVisible = visible;
            int offset = playerInvVisible ? 1000 : -1000;
            for (int i = 0; i < 36; i++) {
                playerInventorySlots.get(i).y += offset;
            }
        }
        return visible;
    }
}
