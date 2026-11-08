package com.enderio.enderio.content.armory.effects.armor;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.conditions.AlwaysActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.CanFreezeHook;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Prevents the wearer from freezing in powdered snow, the same as items in {@link ItemTags#FREEZE_IMMUNE_WEARABLES}, while {@link #condition()} is active.
 */
public class PreventsFreezingEffect extends DarkSteelUpgradeEffect implements CanFreezeHook {

    public static final MapCodec<PreventsFreezingEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            EffectCondition.CODEC.optionalFieldOf("condition", AlwaysActiveCondition.INSTANCE).forGetter(PreventsFreezingEffect::condition)
        ).apply(inst, PreventsFreezingEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.CAN_FREEZE);

    private final EffectCondition condition;

    public PreventsFreezingEffect(EffectCondition condition) {
        this.condition = condition;
    }

    public PreventsFreezingEffect() {
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
    public boolean canFreeze(DarkSteelHookContext context, LivingEntity entity) {
        return !condition.isActive(context.itemStack(), entity.level(), entity);
    }
}
