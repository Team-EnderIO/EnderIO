package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.api.armory.upgrades.costs.CompoundEffectCost;
import com.enderio.enderio.api.armory.upgrades.costs.EffectCost;
import com.enderio.enderio.api.armory.upgrades.costs.EffectDurabilityCost;
import com.enderio.enderio.api.armory.upgrades.costs.EnergyCost;
import com.enderio.enderio.api.armory.upgrades.costs.ExperienceCost;
import com.enderio.enderio.api.armory.upgrades.costs.FreeCost;
import com.enderio.enderio.content.armory.effects.SolarChargeEffect;
import com.enderio.enderio.content.armory.effects.tools.AddItemAbilitiesEffect;
import com.enderio.enderio.content.armory.effects.tools.AdditionalToolRulesEffect;
import com.enderio.enderio.content.armory.effects.tools.ImmediatePickupEffect;
import com.enderio.enderio.content.armory.effects.tools.ShovelBlockTransformationEffect;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIODarkSteelUpgradeEffectCosts {
    private static final DeferredRegister<MapCodec<? extends EffectCost>> DARK_STEEL_UPGRADE_EFFECT_COSTS = DeferredRegister.create(EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT_COST,
        EnderIO.MOD_ID);

    public static void register(IEventBus eventBus) {
        DARK_STEEL_UPGRADE_EFFECT_COSTS.register("no_cost", () -> FreeCost.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_COSTS.register("durability", () -> EffectDurabilityCost.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_COSTS.register("experience", () -> ExperienceCost.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_COSTS.register("energy", () -> EnergyCost.CODEC);
        DARK_STEEL_UPGRADE_EFFECT_COSTS.register("compound", () -> CompoundEffectCost.CODEC);

        DARK_STEEL_UPGRADE_EFFECT_COSTS.register(eventBus);
    }
}
