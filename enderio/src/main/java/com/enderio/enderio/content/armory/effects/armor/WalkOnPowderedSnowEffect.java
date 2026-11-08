package com.enderio.enderio.content.armory.effects.armor;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.conditions.AlwaysActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.WalkOnPowderedSnowHook;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Allows the wearer to walk on powdered snow, the same as leather boots, while {@link #condition()} is active. Only has an effect on boots.
 */
public class WalkOnPowderedSnowEffect extends DarkSteelUpgradeEffect implements WalkOnPowderedSnowHook {

    public static final MapCodec<WalkOnPowderedSnowEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            EffectCondition.CODEC.optionalFieldOf("condition", AlwaysActiveCondition.INSTANCE).forGetter(WalkOnPowderedSnowEffect::condition)
        ).apply(inst, WalkOnPowderedSnowEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.WALK_ON_POWDERED_SNOW);

    private final EffectCondition condition;

    public WalkOnPowderedSnowEffect(EffectCondition condition) {
        this.condition = condition;
    }

    public WalkOnPowderedSnowEffect() {
        this(AlwaysActiveCondition.INSTANCE);
    }

    public EffectCondition condition() {
        return condition;
    }

    @Override
    public List<DarkSteelUpgradeHookType<?>> getActiveHooks() {
        return HOOKS;
    }

    @Override
    protected MapCodec<? extends DarkSteelUpgradeEffect> codec() {
        return CODEC;
    }

    @Override
    public boolean canWalkOnPowderedSnow(DarkSteelHookContext context, LivingEntity wearer) {
        return condition.isActive(context.itemStack(), wearer.level(), wearer);
    }
}
