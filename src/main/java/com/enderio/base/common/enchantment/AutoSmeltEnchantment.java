//package com.enderio.base.common.enchantment;
//
//import net.minecraft.tags.ItemTags;
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.item.enchantment.Enchantment;
//import net.minecraft.world.item.enchantment.Enchantments;
//
//public class AutoSmeltEnchantment extends EIOBaseEnchantment {
//
//    public AutoSmeltEnchantment() {
//        super(
//            definition(
//                ItemTags.MINING_ENCHANTABLE,
//                2,
//                1,
//                constantCost(15),
//                constantCost(60),
//                1,
//                EquipmentSlot.MAINHAND
//            ), () -> true);
//    }
//
//    @Override
//    public boolean isTreasureOnly() {
//        return true;
//    }
//
//    @Override
//    protected boolean checkCompatibility(Enchantment pOther) {
//        return super.checkCompatibility(pOther) && pOther != Enchantments.SILK_TOUCH;
//    }
//}
