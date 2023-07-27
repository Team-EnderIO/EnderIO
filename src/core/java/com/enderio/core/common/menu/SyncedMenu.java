package com.enderio.core.common.menu;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.inventory.InventoryMenu.*;
import static net.minecraft.world.inventory.InventoryMenu.EMPTY_ARMOR_SLOT_HELMET;

public abstract class SyncedMenu<T extends EnderBlockEntity> extends AbstractContainerMenu {

    @Nullable
    private final T blockEntity;
    private final Inventory inventory;

    private final List<Slot> playerInventorySlots = new ArrayList<>();
    private boolean playerInvVisible = true;

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[] { EMPTY_ARMOR_SLOT_BOOTS, EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET };
    private static final EquipmentSlot[] EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
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
    public void addArmorSlots(int xPos, int Ypos) {
        for (int i = 0; i < 4; i++) {
            EquipmentSlot slot = EQUIPMENT_SLOTS[i];
            this.addSlot(new Slot(inventory, 36 + (3 - i), xPos, Ypos + i * 18) {
                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.canEquip(slot, inventory.player);
                }

                @Override
                public boolean mayPickup(Player player) {
                    ItemStack itemstack = this.getItem();
                    return super.mayPickup(player) && !EnchantmentHelper.hasBindingCurse(itemstack);
                }
            }.setBackground(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[slot.getIndex()]));
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
