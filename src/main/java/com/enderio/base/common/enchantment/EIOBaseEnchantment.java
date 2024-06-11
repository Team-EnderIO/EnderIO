package com.enderio.base.common.enchantment;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

public class EIOBaseEnchantment extends Enchantment {

    protected final Supplier<Boolean> enableFlag;

    public EIOBaseEnchantment(EnchantmentDefinition enchantmentDefinition, Supplier<Boolean> flag) {
        super(enchantmentDefinition);
        this.enableFlag = flag;
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
