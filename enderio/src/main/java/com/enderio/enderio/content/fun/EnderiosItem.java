package com.enderio.enderio.content.fun;

import com.enderio.core.common.util.TeleportUtils;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.config.base.BaseConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Calendar;

public class EnderiosItem extends Item {
    public static final Identifier INVERTED_PROPERTY = EnderIO.id("enderios_inverted");

    // TODO: 1.21.4: Does this still have a cooldown?
    private static final FoodProperties PROPERTIES = new FoodProperties.Builder()
        .nutrition(10)
        .saturationModifier(0.8f)
        .build();

    public EnderiosItem(Properties pProperties) {
        super(pProperties.food(PROPERTIES)
            .component(DataComponents.USE_REMAINDER, new UseRemainder(new ItemStack(Items.BOWL))));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        ItemStack itemStack = super.finishUsingItem(pStack, pLevel, pEntityLiving);
        if (pEntityLiving.getRandom().nextFloat() < BaseConfig.COMMON.ITEMS.ENDERIOS_CHANCE.get()) {
            TeleportUtils.randomTeleport(pEntityLiving, BaseConfig.COMMON.ITEMS.ENDERIOS_RANGE.get());
        }
        return itemStack;
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        super.onCraftedBy(stack, player);
        if (player.getUUID().hashCode() == -1435081874 || isSpecialDay()) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal("SOIREDNE"));
        }
    }

    private static boolean isSpecialDay() {
        if (Calendar.getInstance().get(Calendar.MONTH) != Calendar.APRIL)
            return false;
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static class Soiredne implements ConditionalItemModelProperty {
        public static final MapCodec<Soiredne> MAP_CODEC = MapCodec.unit(new Soiredne());

        @Override
        public MapCodec<? extends ConditionalItemModelProperty> type() {
            return MAP_CODEC;
        }

        @Override
        public boolean get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed, ItemDisplayContext displayContext) {
            Component name = stack.get(DataComponents.CUSTOM_NAME);
            return name != null && name.getContents() instanceof PlainTextContents literal && literal.text().equalsIgnoreCase("soiredne");
        }
    }
}
