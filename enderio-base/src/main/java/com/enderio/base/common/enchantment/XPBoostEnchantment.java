package com.enderio.base.common.enchantment;//package com.enderio.base.common.enchantment;
//
//import net.minecraft.tags.ItemTags;
//import net.minecraft.world.entity.EquipmentSlot;
//import net.minecraft.world.item.enchantment.Enchantment;
//import net.minecraft.world.item.enchantment.Enchantments;
//
//public class XPBoostEnchantment extends EIOBaseEnchantment {
//
//    public XPBoostEnchantment() {
//        super(
//            definition(
//                // TODO: 20.6: Might not be right, old filter was damageable that was not armor or fishing rod.
//                ItemTags.WEAPON_ENCHANTABLE,
//                10,
//                3,
//                dynamicCost(1, 10),
//                dynamicCost(30, 10),
//                1,
//                EquipmentSlot.MAINHAND,
//                EquipmentSlot.OFFHAND
//            ), () -> true);
//    }
//
//    @Override
//    protected boolean checkCompatibility(Enchantment pOther) {
//        return super.checkCompatibility(pOther) && pOther != Enchantments.SILK_TOUCH;
//    }
//}
