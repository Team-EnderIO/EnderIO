package com.enderio.core.common.menu;

import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import static net.minecraft.world.inventory.InventoryMenu.*;

public class ArmorSlot extends Slot {

    private static final Identifier[] ARMOR_SLOT_TEXTURES = new Identifier[] { EMPTY_ARMOR_SLOT_BOOTS,
            EMPTY_ARMOR_SLOT_LEGGINGS, EMPTY_ARMOR_SLOT_CHESTPLATE, EMPTY_ARMOR_SLOT_HELMET };

    public ArmorSlot(Container container, int slot, int x, int y, EquipmentSlot equipmentSlot) {
        super(container, slot, x, y);

        setBackground(ARMOR_SLOT_TEXTURES[equipmentSlot.getIndex()]);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack itemstack = this.getItem();
        return (itemstack.isEmpty() || player.isCreative()
                || !EnchantmentHelper.has(itemstack, EnchantmentEffectComponents.PREVENT_ARMOR_CHANGE))
                && super.mayPickup(player);
    }
}
