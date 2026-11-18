package com.enderio.enderio.content.armory.toggles;

import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Active while the given toggle is switched on for the item.
 */
public record ToggledCondition(ResourceLocation toggle) implements EffectCondition {

    public static final MapCodec<ToggledCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            ResourceLocation.CODEC.fieldOf("toggle").forGetter(ToggledCondition::toggle)
        ).apply(inst, ToggledCondition::new));

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        return DarkSteelToggleHelper.isEnabled(itemStack, toggle);
    }
}
