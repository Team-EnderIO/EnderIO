package com.enderio.base.common.item.misc;

import com.enderio.base.common.config.BaseConfig;
import com.enderio.core.common.util.TeleportUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BowlFoodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class EnderiosItem extends BowlFoodItem {
    private static final FoodProperties properties = ((new FoodProperties.Builder()).nutrition(10).saturationMod(0.8f).build());

    public EnderiosItem(Properties pProperties) {
        super(pProperties.food(properties));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        ItemStack itemStack = super.finishUsingItem(pStack, pLevel, pEntityLiving);
        if (pEntityLiving.getRandom().nextFloat() < BaseConfig.COMMON.ITEMS.ENDERIOS_CHANCE.get()) {
            TeleportUtils.randomTeleport(pEntityLiving, BaseConfig.COMMON.ITEMS.ENDERIOS_RANGE.get());
        }
        return itemStack;
    }
}
