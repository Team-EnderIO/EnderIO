package com.enderio.enderio.content.armory.effects;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.conditions.AlwaysActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.general.EquipmentTickHook;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * Applies a mob (potion) effect to the wearer while the item is equipped and {@link #condition()} is active.
 * The effect is given a short duration and topped up while equipped, so it wears off shortly after the item is removed.
 */
public class MobEffectEffect extends DarkSteelUpgradeEffect implements EquipmentTickHook {

    public static final MapCodec<MobEffectEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            MobEffect.CODEC.fieldOf("effect").forGetter(MobEffectEffect::effect),
            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(MobEffectEffect::amplifier),
            Codec.intRange(2, 600).optionalFieldOf("duration", 40).forGetter(MobEffectEffect::duration),
            Codec.intRange(2, 600).optionalFieldOf("reapply_duration_threshold", 20).forGetter(MobEffectEffect::reapplyDurationThreshold),
            Codec.BOOL.optionalFieldOf("show_particles", false).forGetter(MobEffectEffect::showParticles),
            EffectCondition.CODEC.optionalFieldOf("condition", AlwaysActiveCondition.INSTANCE).forGetter(MobEffectEffect::condition)
        ).apply(inst, MobEffectEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.EQUIPMENT_TICK);

    private final Holder<MobEffect> effect;
    private final int amplifier;
    private final int duration;
    private final int reapplyDurationThreshold;
    private final boolean showParticles;
    private final EffectCondition condition;

    public MobEffectEffect(Holder<MobEffect> effect, int amplifier, int duration, int reapplyDurationThreshold, boolean showParticles,
        EffectCondition condition) {
        this.effect = effect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.reapplyDurationThreshold = reapplyDurationThreshold;
        this.showParticles = showParticles;
        this.condition = condition;
    }

    public MobEffectEffect(Holder<MobEffect> effect, int amplifier, int duration, int reapplyDurationThreshold, boolean showParticles) {
        this(effect, amplifier, duration, reapplyDurationThreshold, showParticles, AlwaysActiveCondition.INSTANCE);
    }

    public Holder<MobEffect> effect() {
        return effect;
    }

    public int amplifier() {
        return amplifier;
    }

    public int duration() {
        return duration;
    }

    public int reapplyDurationThreshold() {
        return reapplyDurationThreshold;
    }

    public boolean showParticles() {
        return showParticles;
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
    public void onEquipmentTick(DarkSteelHookContext context, LivingEntity entity, EquipmentSlot slot) {
        if (!condition.isActive(context.itemStack(), entity.level(), entity)) {
            return;
        }

        MobEffectInstance current = entity.getEffect(effect);

        // Only top up once we need to reapply the effect
        if (current != null && (current.getAmplifier() > amplifier || (current.getAmplifier() == amplifier && !current.endsWithin(reapplyDurationThreshold)))) {
            return;
        }

        // Ambient like beacons, as the effect comes from equipment.
        entity.addEffect(new MobEffectInstance(effect, duration, amplifier, true, showParticles, true));
    }
}
