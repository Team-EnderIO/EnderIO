package com.enderio.enderio.api.armory.upgrades.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record NegateCondition(EffectCondition condition) implements EffectCondition {

    public static final MapCodec<NegateCondition> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance
        .group(
            EffectCondition.CODEC.fieldOf("condition").forGetter(NegateCondition::condition)
        ).apply(instance, NegateCondition::new));

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        return !condition.isActive(itemStack, level, entity);
    }
}
