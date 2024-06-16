//package com.enderio.base.common.enchantment;
//
//import com.enderio.base.common.config.BaseConfig;
//import com.enderio.core.common.util.TeleportUtils;
//import net.minecraft.tags.ItemTags;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//
//public class RepellentEnchantment extends EIOBaseEnchantment {
//    public RepellentEnchantment() {
//        super(
//            definition(
//                ItemTags.ARMOR_ENCHANTABLE,
//                4,
//                1,
//                dynamicCost(10, 5),
//                dynamicCost(10, 10),
//                1,
//                EquipmentSlot.CHEST,
//                EquipmentSlot.LEGS,
//                EquipmentSlot.FEET
//            ), () -> true);
//    }
//
//    private float getChance(int level) {
//        return BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_CHANCE_BASE.get().floatValue() + BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_CHANCE_MULT.get().floatValue() * level;
//    }
//
//    private double getRange(int level) {
//        return BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_RANGE_BASE.get() + BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_RANGE_MULT.get() * level;
//    }
//
//    @Override
//    public void doPostHurt(LivingEntity pUser, Entity pAttacker, int pLevel) {
//        if (pUser instanceof Player && pAttacker instanceof LivingEntity attacker) {
//            if (pUser.getRandom().nextFloat() < getChance(pLevel)) {
//                if (pAttacker instanceof Player) {
//                    TeleportUtils.randomTeleport(attacker, getRange(pLevel));
//                } else if (pUser.getRandom().nextFloat() < BaseConfig.COMMON.ENCHANTMENTS.REPELLENT_NON_PLAYER_CHANCE.get()) {
//                    TeleportUtils.randomTeleport(attacker, getRange(pLevel));
//                }
//            }
//        }
//    }
//}
