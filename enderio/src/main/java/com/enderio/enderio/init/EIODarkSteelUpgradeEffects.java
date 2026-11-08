package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.content.armory.effects.tools.AddItemAbilitiesEffect;
import com.enderio.enderio.content.armory.effects.tools.AdditionalToolRulesEffect;
import com.enderio.enderio.content.armory.effects.SolarChargeEffect;
import com.enderio.enderio.content.armory.effects.armor.ElytraEffect;
import com.enderio.enderio.content.armory.effects.tools.ImmediatePickupEffect;
import com.enderio.enderio.content.armory.effects.tools.ShovelBlockTransformationEffect;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIODarkSteelUpgradeEffects {
    private static final DeferredRegister<MapCodec<? extends DarkSteelUpgradeEffect>> DARK_STEEL_UPGRADE_EFFECTS = DeferredRegister.create(EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT,
        EnderIO.MOD_ID);

    public static void register(IEventBus eventBus) {
        DARK_STEEL_UPGRADE_EFFECTS.register("solar_charge", () -> SolarChargeEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("add_item_abilities", () -> AddItemAbilitiesEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("additional_mineable_blocks", () -> AdditionalToolRulesEffect.MinesAndDrops.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("additional_tool_rules", () -> AdditionalToolRulesEffect.Raw.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("shovel_block_transformation", () -> ShovelBlockTransformationEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("immediate_pickup", () -> ImmediatePickupEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("elytra", () -> ElytraEffect.CODEC);

        DARK_STEEL_UPGRADE_EFFECTS.register(eventBus);
    }
}
