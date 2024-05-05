package com.enderio.base.common.enchantment;

import com.enderio.base.common.config.BaseConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class WitherEnchantment extends EIOBaseEnchantment {

    public WitherEnchantment() {
        super(
            definition(
                ItemTags.WEAPON_ENCHANTABLE,
                5,
                1,
                constantCost(1),
                constantCost(100),
                1,
                EquipmentSlot.MAINHAND,
                EquipmentSlot.OFFHAND
            ), () -> true);
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
        if (pTarget instanceof LivingEntity target &&
            pAttacker.getMainHandItem().getEnchantments().getLevel(this) > 0) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        }
    }
}
