package com.enderio.enderio.content.armory.effects.armor;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.costs.EffectCost;
import com.enderio.enderio.api.armory.upgrades.costs.FreeCost;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.armor.ElytraFlyHook;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * Allows the item to act as an elytra, charging {@link #cost()} once every {@link #costTicks()} ticks of flight.
 */
public class ElytraEffect extends DarkSteelUpgradeEffect implements ElytraFlyHook {

    public static final MapCodec<ElytraEffect> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
        .group(
            EffectCost.CODEC.optionalFieldOf("cost", FreeCost.INSTANCE).forGetter(ElytraEffect::cost),
            Codec.intRange(1, 20).optionalFieldOf("cost_ticks", 20).forGetter(ElytraEffect::costTicks)
        ).apply(inst, ElytraEffect::new));

    private static final List<DarkSteelUpgradeHookType<?>> HOOKS = List.of(DarkSteelUpgradeHookTypes.ELYTRA_FLY);

    private final EffectCost cost;
    private final int costTicks;

    public ElytraEffect(EffectCost cost, int costTicks) {
        this.cost = cost;
        this.costTicks = costTicks;
    }

    public EffectCost cost() {
        return cost;
    }

    public int costTicks() {
        return costTicks;
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
    public boolean canElytraFly(DarkSteelHookContext context, LivingEntity entity) {
        if (!(entity instanceof Player)) {
            return false;
        }

        // Costs can only be evaluated on the server. The client optimistically allows flight; if the server refuses
        // to start flying it forces a resync of the fall flying flag (see ServerGamePacketListenerImpl START_FALL_FLYING).
        if (entity instanceof ServerPlayer player) {
            return cost.canAfford(player.serverLevel(), player, context.itemStack());
        }

        return true;
    }

    @Override
    public boolean elytraFlightTick(DarkSteelHookContext context, LivingEntity entity, int flightTicks) {
        if (!(entity instanceof ServerPlayer player)) {
            return true;
        }

        if ((flightTicks + 1) % costTicks != 0) {
            return true;
        }

        return cost.take(player.serverLevel(), player, context.itemStack());
    }
}
