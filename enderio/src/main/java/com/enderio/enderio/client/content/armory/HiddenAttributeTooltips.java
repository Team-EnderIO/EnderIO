package com.enderio.enderio.client.content.armory;

import com.enderio.enderio.content.armory.ItemDarkSteelUpgrades;
import com.enderio.enderio.content.armory.effects.AttributeModifierEffect;
import com.enderio.enderio.init.EIOAttributes;
import com.enderio.enderio.init.EIODataComponents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.GatherSkippedAttributeTooltipsEvent;

/**
 * Hides upgrade modifiers for attributes that are only internal plumbing (e.g. the area mining size), as the upgrade's own
 * description explains them better than a raw attribute line.
 */
@EventBusSubscriber(value = Dist.CLIENT)
public class HiddenAttributeTooltips {

    @SubscribeEvent
    public static void onGatherSkippedAttributeTooltips(GatherSkippedAttributeTooltipsEvent event) {
        ItemDarkSteelUpgrades upgrades = event.getStack().get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null) {
            return;
        }

        for (AttributeModifierEffect effect : upgrades.getAllEffectsOfType(AttributeModifierEffect.class)) {
            if (EIOAttributes.isHiddenFromTooltips(effect.attribute())) {
                event.skipId(effect.modifier().id());
            }
        }
    }
}
