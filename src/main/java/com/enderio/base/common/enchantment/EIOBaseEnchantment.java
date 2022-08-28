package com.enderio.base.common.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.function.Supplier;

public class EIOBaseEnchantment extends Enchantment {

    protected final Supplier<Boolean> enableFlag;

    public EIOBaseEnchantment(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot[] pApplicableSlots, Supplier<Boolean> flag) {
        super(pRarity, pCategory, pApplicableSlots);
        this.enableFlag = flag;
    }

    public EnchantmentCategory getCategory() {
        return this.category;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return super.canApplyAtEnchantingTable(stack) && enableFlag.get();
    }

    @Override
    public boolean canEnchant(ItemStack pStack) {
        return super.canEnchant(pStack) && enableFlag.get();
    }

    @Override
    public boolean isAllowedOnBooks() {
        return super.isAllowedOnBooks() && enableFlag.get();
    }
}
