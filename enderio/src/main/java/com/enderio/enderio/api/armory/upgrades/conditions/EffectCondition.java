package com.enderio.enderio.api.armory.upgrades.conditions;

import com.enderio.enderio.api.EnderIORegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface EffectCondition {
    Codec<EffectCondition> CODEC = EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT_CONDITION.byNameCodec()
        .dispatchStable(EffectCondition::codec, Function.identity());

    MapCodec<? extends EffectCondition> codec();

    boolean isActive(ItemStack itemStack, @Nullable Level level, @Nullable Entity entity);
}
