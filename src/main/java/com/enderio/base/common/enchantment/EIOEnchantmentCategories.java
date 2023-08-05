package com.enderio.base.common.enchantment;

import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class EIOEnchantmentCategories {
    public static final EnchantmentCategory XPBOOST = EnchantmentCategory.create("EIO_XPBOOST",
        t -> new ItemStack(t).isDamageableItem() && !(t instanceof ArmorItem) && !(t instanceof FishingRodItem));

    public static final EnchantmentCategory PROJECTILE = EnchantmentCategory.create("EIO_PROJECTILE",
        t -> EnchantmentCategory.BOW.canEnchant(t) || EnchantmentCategory.CROSSBOW.canEnchant(t));

    public static final EnchantmentCategory WEAPON = EnchantmentCategory.create("EIO_PROJECTILE",
        t -> EnchantmentCategory.BOW.canEnchant(t) || EnchantmentCategory.CROSSBOW.canEnchant(t) || EnchantmentCategory.WEAPON.canEnchant(t));

}
