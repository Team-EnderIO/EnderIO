package com.enderio.base.common.enchantment;

import com.enderio.base.common.config.BaseConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class ShimmerEnchantment extends EIOBaseEnchantment {

    public ShimmerEnchantment() {
        super(
            definition(
                ItemTags.VANISHING_ENCHANTABLE,
                1,
                1,
                constantCost(1),
                constantCost(100),
                1,
                EquipmentSlot.values()
            ), () -> true);
    }

    @Override
    public boolean isTreasureOnly() {
        return true;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return false;
    }
}
