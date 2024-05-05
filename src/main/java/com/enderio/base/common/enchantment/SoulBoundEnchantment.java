package com.enderio.base.common.enchantment;

import com.enderio.base.common.config.BaseConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;

public class SoulBoundEnchantment extends EIOBaseEnchantment {

    // TODO: 20.6: all enchantments these need proper anvil costs.
    public SoulBoundEnchantment() {
        super(
            definition(
                ItemTags.MINING_ENCHANTABLE,
                2,
                1,
                constantCost(BaseConfig.COMMON.ENCHANTMENTS.SOUL_BOUND_MIN_COST.get()),
                constantCost(BaseConfig.COMMON.ENCHANTMENTS.SOUL_BOUND_MAX_COST.get()),
                1,
                EquipmentSlot.values()
            ), () -> true);
    }
}
