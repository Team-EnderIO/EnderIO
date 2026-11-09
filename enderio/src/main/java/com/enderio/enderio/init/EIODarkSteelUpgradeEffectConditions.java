package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.conditions.AllOfCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.AlwaysActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.AnyOfCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.DayTimeRangeCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EffectCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.EnergyRangeCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.HasCapacitorCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.NegateCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.NeverActiveCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.SkyAccessCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.SteppingCarefullyCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIODarkSteelUpgradeEffectConditions {
    private static final DeferredRegister<MapCodec<? extends EffectCondition>> DARK_STEEL_UPGRADE_EFFECT_CONDITIONS = DeferredRegister.create(EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT_CONDITION,
        EnderIO.MOD_ID);

    public static void register(IEventBus eventBus) {
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("always", () -> AlwaysActiveCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("never", () -> NeverActiveCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("not", () -> NegateCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("all", () -> AllOfCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("any", () -> AnyOfCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("has_capacitor", () -> HasCapacitorCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("energy_within", () -> EnergyRangeCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("entity_stepping_carefully", () -> SteppingCarefullyCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("entity_can_see_sky", () -> SkyAccessCondition.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register("day_time_within", () -> DayTimeRangeCondition.CODEC);

        DARK_STEEL_UPGRADE_EFFECT_CONDITIONS.register(eventBus);
    }
}
