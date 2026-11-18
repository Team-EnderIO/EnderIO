package com.enderio.enderio.api.armory.upgrades.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record AnyOfCondition(List<EffectCondition> conditions) implements EffectCondition {

    public static final MapCodec<AnyOfCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            EffectCondition.CODEC.listOf(0, 32).fieldOf("conditions").forGetter(AnyOfCondition::conditions)
        ).apply(inst, AnyOfCondition::new));

    @Override
    public MapCodec<? extends EffectCondition> codec() {
        return CODEC;
    }

    @Override
    public boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity) {
        for (EffectCondition condition : this.conditions) {
            if (condition.isActive(itemStack, level, entity)) {
                return true;
            }
        }

        return false;
    }
}
