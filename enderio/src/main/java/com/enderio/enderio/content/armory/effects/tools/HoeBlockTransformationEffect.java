package com.enderio.enderio.content.armory.effects.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.conditions.AlwaysActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.CanPerformActionHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseOnHook;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class HoeBlockTransformationEffect extends DarkSteelUpgradeEffect implements CanPerformActionHook, UseOnHook {

    public static final MapCodec<HoeBlockTransformationEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            EffectCondition.CODEC.optionalFieldOf("condition", AlwaysActiveCondition.INSTANCE).forGetter(HoeBlockTransformationEffect::condition)
        ).apply(inst, HoeBlockTransformationEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.CAN_PERFORM_ACTION,
        DarkSteelUpgradeHookTypes.USE_ON);

    private final EffectCondition condition;

    public HoeBlockTransformationEffect(EffectCondition condition) {
        this.condition = condition;
    }

    public HoeBlockTransformationEffect() {
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
    public boolean canPerformAction(DarkSteelHookContext context, ItemAbility itemAbility) {
        return (itemAbility == ItemAbilities.HOE_TILL)
            && condition.isActive(context.itemStack(), null, null);
    }

    @Override
    public InteractionResult useOn(DarkSteelHookContext context, UseOnContext useOnContext, InteractionResult currentResult) {
        if (currentResult != InteractionResult.PASS
            || !condition.isActive(context.itemStack(), useOnContext.getLevel(), useOnContext.getPlayer())) {
            return currentResult;
        }

        // TODO: Reimplement directly for nice-ness
        return Items.DIAMOND_HOE.useOn(useOnContext);
    }
}
