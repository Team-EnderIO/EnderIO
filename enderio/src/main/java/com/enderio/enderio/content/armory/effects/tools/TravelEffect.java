package com.enderio.enderio.content.armory.effects.tools;

import com.enderio.enderio.api.EnderIOItemAbilities;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.costs.EffectCost;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.CanPerformActionHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseHook;
import com.enderio.enderio.api.armory.upgrades.hooks.tools.UseOnHook;
import com.enderio.enderio.content.travel.TravelHandler;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class TravelEffect extends DarkSteelUpgradeEffect implements UseOnHook, UseHook, CanPerformActionHook {

    public static final MapCodec<TravelEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(Codec.BOOL.fieldOf("require_crouched").forGetter(TravelEffect::showTargetsWhenCrouched),
            EffectCost.CODEC.fieldOf("cost_per_travel").forGetter(TravelEffect::cost)
        ).apply(inst, TravelEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.USE, DarkSteelUpgradeHookTypes.USE_ON,
        DarkSteelUpgradeHookTypes.CAN_PERFORM_ACTION);

    private final boolean requireCrouched;
    private final EffectCost cost;
    // TODO: Travel costs etc.

    public TravelEffect(boolean requireCrouched, EffectCost cost) {
        this.requireCrouched = requireCrouched;
        this.cost = cost;
    }

    public boolean showTargetsWhenCrouched() {
        return requireCrouched;
    }

    public EffectCost cost() {
        return cost;
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
    public InteractionResultHolder<ItemStack> use(DarkSteelHookContext context, Level level, Player player, InteractionHand usedHand,
        InteractionResultHolder<ItemStack> currentResult) {
        if (currentResult.getResult() != InteractionResult.PASS) {
            return currentResult;
        }

        // TODO: Implement short-range travel like the staff

        if (requireCrouched && !player.isShiftKeyDown()) {
            return currentResult;
        }

        if (!cost.canAfford(level, player, currentResult.getObject())) {
            return currentResult;
        }

        if (TravelHandler.blockTeleport(level, player)) {
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                // Modifies the stack in-place
                cost.take(serverLevel, serverPlayer, currentResult.getObject());
            }

            return InteractionResultHolder.sidedSuccess(currentResult.getObject(), level.isClientSide());
        }

        return currentResult;
    }

    @Override
    public InteractionResult useOn(DarkSteelHookContext context, UseOnContext useOnContext, InteractionResult currentResult) {
        if (currentResult != InteractionResult.PASS) {
            return currentResult;
        }

        // TODO: Implement short-range travel like the staff

        var level = useOnContext.getLevel();
        var player = useOnContext.getPlayer();
        if (player == null) {
            return currentResult;
        }

        if (requireCrouched && !player.isShiftKeyDown()) {
            return currentResult;
        }

        if (!cost.canAfford(level, player, useOnContext.getItemInHand())) {
            return currentResult;
        }

        if (TravelHandler.blockTeleport(level, player)) {
            if (level instanceof ServerLevel serverLevel && player instanceof ServerPlayer serverPlayer) {
                // Modifies the stack in-place
                cost.take(serverLevel, serverPlayer, useOnContext.getItemInHand());
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return currentResult;
    }

    @Override
    public boolean canPerformAction(DarkSteelHookContext context, ItemAbility itemAbility) {
        return itemAbility == (requireCrouched ? EnderIOItemAbilities.SEE_TRAVEL_TARGETS_CROUCHED : EnderIOItemAbilities.SEE_TRAVEL_TARGETS);
    }
}
