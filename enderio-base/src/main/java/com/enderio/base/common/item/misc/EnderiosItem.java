package com.enderio.base.common.item.misc;

import com.enderio.base.common.config.BaseConfig;
import com.enderio.core.common.util.TeleportUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;


public class EnderiosItem extends Item {
    private static final FoodProperties PROPERTIES = new FoodProperties.Builder()
        .nutrition(10)
        .saturationModifier(0.8f)
        .usingConvertsTo(Items.BOWL)
        .build();

    public EnderiosItem(Properties pProperties) {
        super(pProperties.food(PROPERTIES));
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
