package com.enderio.core.common.menu;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
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
}
