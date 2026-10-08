package com.enderio.enderio.content.fun;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import org.jspecify.annotations.Nullable;

import java.util.Calendar;

public class EnderiosItem extends Item {
    private static final FoodProperties PROPERTIES = new FoodProperties.Builder()
        .nutrition(10)
        .saturationModifier(0.8f)
        .build();

    private static final Consumable CONSUMABLE = Consumables.defaultFood().onConsume(EnderiosConsumeEffect.INSTANCE).build();

    public EnderiosItem(Properties properties) {
        super(properties.stacksTo(1).food(PROPERTIES, CONSUMABLE).usingConvertsTo(Items.BOWL));
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        super.onCraftedBy(stack, player);
        if (player.getUUID().hashCode() == -1435081874 || isSpecialDay()) {
            stack.set(DataComponents.CUSTOM_NAME, Component.literal("SOIREDNE"));
        }
    }

    private static boolean isSpecialDay() {
        return Calendar.getInstance().get(Calendar.MONTH) == Calendar.APRIL &&
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1;
    }

    public static class SoiredneItemProperty implements ConditionalItemModelProperty {
        public static final MapCodec<SoiredneItemProperty> MAP_CODEC = MapCodec.unit(new SoiredneItemProperty());

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
