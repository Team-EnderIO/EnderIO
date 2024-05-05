package com.enderio.base.common.enchantment;

import com.enderio.base.common.config.BaseConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class XPBoostEnchantment extends EIOBaseEnchantment {

    public XPBoostEnchantment() {
        super(
            definition(
                // TODO: 20.6: Might not be right, old filter was damageable that was not armor or fishing rod.
                ItemTags.WEAPON_ENCHANTABLE,
                10,
                3,
                dynamicCost(
                    BaseConfig.COMMON.ENCHANTMENTS.XP_BOOST_MIN_COST_BASE.get(),
                    BaseConfig.COMMON.ENCHANTMENTS.XP_BOOST_MIN_COST_MULT.get()
                ),
                dynamicCost(
                    BaseConfig.COMMON.ENCHANTMENTS.XP_BOOST_MAX_COST_BASE.get(),
                    BaseConfig.COMMON.ENCHANTMENTS.XP_BOOST_MAX_COST_MULT.get()
                ),
                1,
                EquipmentSlot.MAINHAND,
                EquipmentSlot.OFFHAND
            ), () -> true);
    }

    @Override
    protected boolean checkCompatibility(Enchantment pOther) {
        return super.checkCompatibility(pOther) && pOther != Enchantments.SILK_TOUCH;
    }
}
