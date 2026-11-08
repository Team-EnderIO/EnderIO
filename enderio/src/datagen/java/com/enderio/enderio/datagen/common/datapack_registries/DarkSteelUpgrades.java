package com.enderio.enderio.datagen.common.datapack_registries;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgrade;
import com.enderio.enderio.api.armory.upgrades.costs.EffectDurabilityCost;
import com.enderio.enderio.api.armory.upgrades.costs.FreeCost;
import com.enderio.enderio.content.armory.effects.AttributeModifierEffect;
import com.enderio.enderio.content.armory.effects.armor.ElytraEffect;
import com.enderio.enderio.content.armory.effects.tools.AddItemAbilitiesEffect;
import com.enderio.enderio.content.armory.effects.tools.AdditionalToolRulesEffect;
import com.enderio.enderio.content.armory.effects.tools.HoeBlockTransformationEffect;
import com.enderio.enderio.content.armory.effects.tools.ImmediatePickupEffect;
import com.enderio.enderio.content.armory.effects.tools.ShovelBlockTransformationEffect;
import com.enderio.enderio.content.armory.effects.tools.TravelEffect;
import com.enderio.enderio.foundation.tag.EIOTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ItemAbilities;

import java.util.List;

public class DarkSteelUpgrades {
    public static void bootstrap(BootstrapContext<DarkSteelUpgrade> context) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        context.register(key("spoon"), new DarkSteelUpgrade(Component.literal("Spoon"), Component.literal("Who needs a shovel when you have a spoon..."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new AddItemAbilitiesEffect(ItemAbilities.SHOVEL_DIG),
                new AdditionalToolRulesEffect.MinesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL),
                ShovelBlockTransformationEffect.INSTANCE
        )));

        context.register(key("fork"), new DarkSteelUpgrade(Component.literal("Fork"), Component.literal("Who needs a hoe when you have a fork..."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new AddItemAbilitiesEffect(ItemAbilities.HOE_DIG),
                new AdditionalToolRulesEffect.MinesAndDrops(BlockTags.MINEABLE_WITH_HOE),
                HoeBlockTransformationEffect.INSTANCE
        )));

        context.register(key("direct"), new DarkSteelUpgrade(Component.literal("Direct"), Component.literal("Teleports mined things directly into your inventory"),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
            new ImmediatePickupEffect(new EffectDurabilityCost(1))
        )));

        context.register(key("elytra"), new DarkSteelUpgrade(Component.literal("Elytra"), Component.literal("Makes armor work as elytra"),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_CHESTPLATES), false),
            HolderSet.empty(), List.of(new ElytraEffect(FreeCost.INSTANCE, 20)
        )));

        // Step height base is 0.6, so +0.4 lets the wearer step up full blocks.
        context.register(key("step_assist"), new DarkSteelUpgrade(Component.literal("Step Assist"), Component.literal("Allows you to walk up blocks as if they were stairs."),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_BOOTS), false), HolderSet.empty(), List.of(
                new AttributeModifierEffect(Attributes.STEP_HEIGHT,
                    new AttributeModifier(EnderIO.rl("dark_steel_upgrade/step_assist"), 0.4, AttributeModifier.Operation.ADD_VALUE))
        )));

        context.register(key("travel"), new DarkSteelUpgrade(Component.literal("Travel"), Component.literal("Hold Shift and Right-Click to teleport or activate a travel anchor"),
            new DarkSteelUpgrade.UpgradeDefinition(items.getOrThrow(EIOTags.Items.DARK_STEEL_UPGRADABLE_TOOLS), false), HolderSet.empty(), List.of(
                new TravelEffect(true, new EffectDurabilityCost(5))
        )));
    }

    private static ResourceKey<DarkSteelUpgrade> key(String name) {
        return ResourceKey.create(EnderIORegistries.Keys.DARK_STEEL_UPGRADE, EnderIO.rl(name));
    }
}
