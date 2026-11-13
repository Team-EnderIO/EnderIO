package com.enderio.enderio.client.content.armory;

import com.enderio.enderio.content.armory.ItemDarkSteelUpgrades;
import com.enderio.enderio.content.armory.effects.AttributeModifierEffect;
import com.enderio.enderio.init.EIODataComponents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.GatherSkippedAttributeTooltipsEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class HiddenAttributeTooltips {

    @SubscribeEvent
    public static void onGatherSkippedAttributeTooltips(GatherSkippedAttributeTooltipsEvent event) {
        ItemDarkSteelUpgrades upgrades = event.getStack().get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null) {
            return;
        }

        // All attributes added by upgrades are suppressed to reduce tooltip noise
        for (AttributeModifierEffect effect : upgrades.getAllEffectsOfType(AttributeModifierEffect.class)) {
            if (effect.hideFromTooltip()) {
                event.skipId(effect.modifier().id());
            }
        }
    }
}
