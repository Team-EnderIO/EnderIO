package com.enderio.enderio.datagen.common.datapack_registries;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.DarkSteelToggle;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgrade;
import com.enderio.enderio.api.armory.upgrades.conditions.AllOfCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.DayTimeRangeCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.NegateCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.SkyAccessCondition;
import com.enderio.enderio.api.armory.upgrades.conditions.SteppingCarefullyCondition;
import com.enderio.enderio.api.armory.upgrades.costs.EffectDurabilityCost;
import com.enderio.enderio.api.armory.upgrades.costs.FreeCost;
import com.enderio.enderio.content.armory.effects.AttributeModifierEffect;
import com.enderio.enderio.content.armory.effects.ChargeInventoryEffect;
import com.enderio.enderio.content.armory.effects.MobEffectEffect;
import com.enderio.enderio.content.armory.effects.armor.ElytraEffect;
import com.enderio.enderio.content.armory.effects.armor.PreventsFreezingEffect;
import com.enderio.enderio.content.armory.effects.armor.WalkOnPowderedSnowEffect;
import com.enderio.enderio.content.armory.effects.tools.AddItemAbilitiesEffect;
import com.enderio.enderio.content.armory.effects.tools.AdditionalToolRulesEffect;
import com.enderio.enderio.content.armory.effects.tools.AreaMiningEffect;
import com.enderio.enderio.content.armory.effects.tools.HoeBlockTransformationEffect;
import com.enderio.enderio.content.armory.effects.tools.ImmediatePickupEffect;
import com.enderio.enderio.content.armory.effects.tools.ShovelBlockTransformationEffect;
import com.enderio.enderio.content.armory.effects.tools.TravelEffect;
import com.enderio.enderio.content.armory.toggles.DarkSteelToggleKeys;
import com.enderio.enderio.content.armory.toggles.ToggledCondition;
import com.enderio.enderio.content.machines.solar_panel.SolarPanelBlockEntity;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.init.EIOAttributes;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DarkSteelUpgrades {
    public static void bootstrap(BootstrapContext<DarkSteelUpgrade> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        HolderGetter<Block> blocks = context.lookup(Registries.BLOCK);

        context.register(key("spoon"), new DarkSteelUpgrade(Component.literal("Spoon"), Component.literal("Who needs a shovel when you have a spoon..."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new AddItemAbilitiesEffect(ItemAbilities.SHOVEL_DIG),
                new AdditionalToolRulesEffect.MinesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL),
                new ShovelBlockTransformationEffect()
        )));

        context.register(key("fork"), new DarkSteelUpgrade(Component.literal("Fork"), Component.literal("Who needs a hoe when you have a fork..."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new AddItemAbilitiesEffect(ItemAbilities.HOE_DIG),
                new AdditionalToolRulesEffect.MinesAndDrops(BlockTags.MINEABLE_WITH_HOE),
                new HoeBlockTransformationEffect()
        )));

        context.register(key("direct"), new DarkSteelUpgrade(Component.literal("Direct"), Component.literal("Teleports mined things directly into your inventory"),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
            new ImmediatePickupEffect(new EffectDurabilityCost(1))
        )));

        ResourceLocation elytraToggle = EnderIO.rl("elytra");
        context.register(key("elytra"), new DarkSteelUpgrade(Component.literal("Elytra"), Component.literal("Makes armor work as elytra"),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_CHESTPLATES), false),
            HolderSet.empty(), List.of(new ElytraEffect(FreeCost.INSTANCE, 20, new ToggledCondition(elytraToggle))
            ), Map.of(
                elytraToggle, new DarkSteelToggle(Component.literal("Elytra"), true, Optional.of(DarkSteelToggleKeys.ELYTRA))
        )));

        ResourceLocation stepAssistToggle = EnderIO.rl("step_assist");
        // Step height base is 0.6, so +0.4 lets the wearer step up full blocks.
        context.register(key("step_assist"), new DarkSteelUpgrade(Component.literal("Step Assist"), Component.literal("Allows you to walk up blocks as if they were stairs."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_BOOTS), false), HolderSet.empty(), List.of(
                new AttributeModifierEffect(Attributes.STEP_HEIGHT,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/step_assist"), 0.4, AttributeModifier.Operation.ADD_VALUE),
                    new ToggledCondition(stepAssistToggle))
            ), Map.of(
                stepAssistToggle, new DarkSteelToggle(Component.literal("Step Assist"), true, Optional.of(DarkSteelToggleKeys.STEP_ASSIST))
        )));

        context.register(key("travel"), new DarkSteelUpgrade(Component.literal("Travel"), Component.literal("Hold Shift and Right-Click to teleport or activate a travel anchor"),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new TravelEffect(true, new EffectDurabilityCost(5))
        )));

        ResourceLocation explosiveToggle = EnderIO.rl("explosive");
        context.register(key("explosive"), new DarkSteelUpgrade(Component.literal("Explosive"), Component.literal("Blows up the stone and dirt around the block you mine. Crouch to mine a single block"),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new AreaMiningEffect(Optional.of(blocks.getOrThrow(EIOTags.Blocks.AREA_MINING_TARGETS)), FreeCost.INSTANCE, new AllOfCondition(List.of(
                    new ToggledCondition(explosiveToggle),
                    new NegateCondition(SteppingCarefullyCondition.INSTANCE))), true, true, true),
                new AttributeModifierEffect(EIOAttributes.MINING_AREA_RADIUS,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/explosive"), 1, AttributeModifier.Operation.ADD_VALUE))
            ), Map.of(
                explosiveToggle, new DarkSteelToggle(Component.literal("Explosive"), true, Optional.of(DarkSteelToggleKeys.EXPLOSIVE))
        )));

        context.register(key("explosive_depth"), new DarkSteelUpgrade(Component.literal("Depth"), Component.literal("Directs the explosive force away from the player."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new AttributeModifierEffect(EIOAttributes.MINING_AREA_DEPTH,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/explosive_depth"), 2, AttributeModifier.Operation.ADD_VALUE))
        )));

        context.register(key("explosive_carpet"), new DarkSteelUpgrade(Component.literal("Carpeting"), Component.literal("Flattens the explosive force into a carpet."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new AttributeModifierEffect(EIOAttributes.MINING_AREA_RADIUS,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/explosive_carpet"), 1, AttributeModifier.Operation.ADD_VALUE))
        )));

        ResourceLocation doubleJumpToggle = EnderIO.rl("double_jump");
        context.register(key("double_jump"), new DarkSteelUpgrade(Component.literal("Double Jump"), Component.literal("Allows you to jump again while in the air."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_BOOTS), false), HolderSet.empty(), List.of(
                new AttributeModifierEffect(EIOAttributes.EXTRA_JUMPS,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/jump"), 1, AttributeModifier.Operation.ADD_VALUE),
                    new ToggledCondition(doubleJumpToggle))
            ), Map.of(
                doubleJumpToggle, new DarkSteelToggle(Component.literal("Double Jump"), true, Optional.of(DarkSteelToggleKeys.DOUBLE_JUMP))
        )));

        context.register(key("jump_boost"), new DarkSteelUpgrade(Component.literal("Jump Boost"), Component.literal("Increases your jump power."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_BOOTS), false), HolderSet.empty(), List.of(
                new AttributeModifierEffect(Attributes.JUMP_STRENGTH,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/jump_boost"), 0.35F, AttributeModifier.Operation.ADD_VALUE))
        )));

        // Matches 1.12's Speed I: +15% of base movement speed.
        ResourceLocation speedToggle = EnderIO.rl("speed");
        context.register(key("speed"), new DarkSteelUpgrade(Component.literal("Speed"), Component.literal("Increases your movement speed."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_LEGGINGS), false), HolderSet.empty(), List.of(
                new AttributeModifierEffect(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/speed"), 0.15, AttributeModifier.Operation.ADD_MULTIPLIED_BASE),
                    new ToggledCondition(speedToggle))
            ), Map.of(
                speedToggle, new DarkSteelToggle(Component.literal("Speed"), true, Optional.of(DarkSteelToggleKeys.SPEED))
        )));

        context.register(key("reach"), new DarkSteelUpgrade(Component.literal("Reach"), Component.literal("Lets you reach blocks further away while held."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new AttributeModifierEffect(Attributes.BLOCK_INTERACTION_RANGE,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/reach"), 2, AttributeModifier.Operation.ADD_VALUE))
        )));

        context.register(key("stable_footing"), new DarkSteelUpgrade(Component.literal("Stable Footing"), Component.literal("Prevents the wearer from falling through powdered snow."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_BOOTS), false), HolderSet.empty(), List.of(
                new WalkOnPowderedSnowEffect()
        )));

        context.register(key("insulation"), new DarkSteelUpgrade(Component.literal("Insulation"), Component.literal("Prevents the wearer from falling freezing in powdered snow."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_ARMOR), false), HolderSet.empty(), List.of(
                new PreventsFreezingEffect()
        )));

        ResourceLocation nightVisionToggle = EnderIO.rl("night_vision_enabled");
        context.register(key("night_vision"), new DarkSteelUpgrade(Component.literal("Night Vision"), Component.literal("Gives the user night vision."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_HELMETS), false), HolderSet.empty(), List.of(
                // at 10s left the player's vision will begin flashing
                new MobEffectEffect(MobEffects.NIGHT_VISION, 0, 15 * 20, 12 * 20, false, new ToggledCondition(nightVisionToggle))
            ), Map.of(
                nightVisionToggle, new DarkSteelToggle(Component.literal("Night Vision"), true, Optional.of(DarkSteelToggleKeys.NIGHT_VISION))
        )));

        context.register(key("solar"), new DarkSteelUpgrade(Component.literal("Solar"), Component.literal("Charges your dark steel armor and held tools while in direct sunlight."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_HELMETS), false), HolderSet.empty(), List.of(
                new ChargeInventoryEffect(4, 20, true, true, true, false, Optional.of(EIOTags.Items.DARK_STEEL_UPGRADABLE),
                    new AllOfCondition(List.of(
                        SkyAccessCondition.INSTANCE,
                        new DayTimeRangeCondition(0, SolarPanelBlockEntity.DAYTIME_LENGTH - 1)
                    )))
        )));
    }

    private static ResourceKey<DarkSteelUpgrade> key(String name) {
        return ResourceKey.create(EnderIORegistries.Keys.DARK_STEEL_UPGRADE, EnderIO.rl(name));
    }
}
