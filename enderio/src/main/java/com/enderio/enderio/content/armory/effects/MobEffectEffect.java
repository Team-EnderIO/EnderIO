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
import net.neoforged.neoforge.common.EffectCure;

import java.util.List;

/**
 * Applies a mob (potion) effect to the wearer while the item is equipped and {@link #condition()} is active.
 * The effect is given a short duration and topped up while equipped, so it wears off shortly after the item is removed.
 * <p>
 * Applied effects carry {@link #UPGRADE_EFFECT_CURE} so they can be told apart from effects given by other sources,
 * and are removed as soon as the condition stops being active.
 * Unless {@link #retainDefaultCures()} is set, the effect's default cures (e.g. milk) are dropped, as the upgrade would just reapply it.
 */
public class MobEffectEffect extends DarkSteelUpgradeEffect implements EquipmentTickHook {

    /**
     * Marks effect instances applied by dark steel upgrades.
     */
    public static final EffectCure UPGRADE_EFFECT_CURE = EffectCure.get("enderio:dark_steel_upgrade");

    public static final MapCodec<MobEffectEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            MobEffect.CODEC.fieldOf("effect").forGetter(MobEffectEffect::effect),
            Codec.intRange(0, 255).optionalFieldOf("amplifier", 0).forGetter(MobEffectEffect::amplifier),
            Codec.intRange(2, 600).optionalFieldOf("duration", 40).forGetter(MobEffectEffect::duration),
            Codec.intRange(2, 600).optionalFieldOf("reapply_duration_threshold", 20).forGetter(MobEffectEffect::reapplyDurationThreshold),
            Codec.BOOL.optionalFieldOf("show_particles", false).forGetter(MobEffectEffect::showParticles),
            EffectCondition.CODEC.optionalFieldOf("condition", AlwaysActiveCondition.INSTANCE).forGetter(MobEffectEffect::condition),
            Codec.BOOL.optionalFieldOf("retain_default_cures", false).forGetter(MobEffectEffect::retainDefaultCures)
        ).apply(inst, MobEffectEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.EQUIPMENT_TICK);

    private final Holder<MobEffect> effect;
    private final int amplifier;
    private final int duration;
    private final int reapplyDurationThreshold;
    private final boolean showParticles;
    private final EffectCondition condition;
    private final boolean retainDefaultCures;

    public MobEffectEffect(Holder<MobEffect> effect, int amplifier, int duration, int reapplyDurationThreshold, boolean showParticles,
        EffectCondition condition, boolean retainDefaultCures) {
        this.effect = effect;
        this.amplifier = amplifier;
        this.duration = duration;
        this.reapplyDurationThreshold = reapplyDurationThreshold;
        this.showParticles = showParticles;
        this.condition = condition;
        this.retainDefaultCures = retainDefaultCures;
    }

    public MobEffectEffect(Holder<MobEffect> effect, int amplifier, int duration, int reapplyDurationThreshold, boolean showParticles,
        EffectCondition condition) {
        this(effect, amplifier, duration, reapplyDurationThreshold, showParticles, condition, false);
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

    public boolean retainDefaultCures() {
        return retainDefaultCures;
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
        MobEffectInstance current = entity.getEffect(effect);
        if (!condition.isActive(context.itemStack(), entity.level(), entity)) {
            // Clear the effect straight away rather than letting it run out, but only if we applied it.
            if (current != null && isOwnEffect(current)) {
                entity.removeEffect(effect);
            }

            return;
        }

        // Only top up once we need to reapply the effect
        if (current != null && (current.getAmplifier() > amplifier || (current.getAmplifier() == amplifier && !current.endsWithin(reapplyDurationThreshold)))) {
            return;
        }

        // Ambient like beacons, as the effect comes from equipment.
        var instance = new MobEffectInstance(effect, duration, amplifier, true, showParticles, true);
        if (!retainDefaultCures) {
            instance.getCures().clear();
        }

        instance.getCures().add(UPGRADE_EFFECT_CURE);
        entity.addEffect(instance);
    }

    /**
     * An effect is ours if it's cured by the upgrade & is ambient.
     */
    private boolean isOwnEffect(MobEffectInstance instance) {
        return instance.getCures().contains(UPGRADE_EFFECT_CURE)
            && instance.isAmbient()
            && instance.getAmplifier() == amplifier;
    }
}
