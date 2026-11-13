package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeEffect;
import com.enderio.enderio.content.armory.effects.tools.AddItemAbilitiesEffect;
import com.enderio.enderio.content.armory.effects.tools.AdditionalToolRulesEffect;
import com.enderio.enderio.content.armory.effects.tools.AreaMiningEffect;
import com.enderio.enderio.content.armory.effects.AttributeModifierEffect;
import com.enderio.enderio.content.armory.effects.MobEffectEffect;
import com.enderio.enderio.content.armory.effects.ChargeInventoryEffect;
import com.enderio.enderio.content.armory.effects.armor.ElytraEffect;
import com.enderio.enderio.content.armory.effects.armor.PreventsFreezingEffect;
import com.enderio.enderio.content.armory.effects.armor.WalkOnPowderedSnowEffect;
import com.enderio.enderio.content.armory.effects.tools.HoeBlockTransformationEffect;
import com.enderio.enderio.content.armory.effects.tools.ImmediatePickupEffect;
import com.enderio.enderio.content.armory.effects.tools.ShovelBlockTransformationEffect;
import com.enderio.enderio.content.armory.effects.tools.TravelEffect;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EIODarkSteelUpgradeEffects {
    private static final DeferredRegister<MapCodec<? extends DarkSteelUpgradeEffect>> DARK_STEEL_UPGRADE_EFFECTS = DeferredRegister.create(EnderIORegistries.DARK_STEEL_UPGRADE_EFFECT,
        EnderIO.MOD_ID);

    public static void register(IEventBus eventBus) {
        DARK_STEEL_UPGRADE_EFFECTS.register("charge_inventory", () -> ChargeInventoryEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("attribute_modifier", () -> AttributeModifierEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("mob_effect", () -> MobEffectEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("add_item_abilities", () -> AddItemAbilitiesEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("additional_mineable_blocks", () -> AdditionalToolRulesEffect.MinesAndDrops.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("additional_tool_rules", () -> AdditionalToolRulesEffect.Raw.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("shovel_block_transformation", () -> ShovelBlockTransformationEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("hoe_block_transformation", () -> HoeBlockTransformationEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("immediate_pickup", () -> ImmediatePickupEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("elytra", () -> ElytraEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("prevents_freezing", () -> PreventsFreezingEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("walk_on_powdered_snow", () -> WalkOnPowderedSnowEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("travel", () -> TravelEffect.CODEC);
        DARK_STEEL_UPGRADE_EFFECTS.register("area_mining", () -> AreaMiningEffect.CODEC);

        DARK_STEEL_UPGRADE_EFFECTS.register(eventBus);
    }
}
