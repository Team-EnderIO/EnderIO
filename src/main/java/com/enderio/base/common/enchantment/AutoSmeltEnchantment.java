package com.enderio.base.common.enchantment;

import com.enderio.base.common.config.BaseConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class AutoSmeltEnchantment extends EIOBaseEnchantment {

    public AutoSmeltEnchantment() {
        super(
            definition(
                ItemTags.MINING_ENCHANTABLE,
                2,
                1,
                constantCost(BaseConfig.COMMON.ENCHANTMENTS.AUTO_SMELT_MIN_COST.get()),
                constantCost(BaseConfig.COMMON.ENCHANTMENTS.AUTO_SMELT_MAX_COST.get()),
                1,
                EquipmentSlot.MAINHAND
            ), () -> true);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && pOther != Enchantments.SILK_TOUCH;
    }
}
