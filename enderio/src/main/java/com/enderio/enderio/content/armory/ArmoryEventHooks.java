package com.enderio.enderio.content.armory;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradableItem;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import com.enderio.enderio.api.armory.upgrades.hooks.general.ModifyAttributesHook;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@EventBusSubscriber
public class ArmoryEventHooks {
    // This is the one point every stack's modifiers pass through, whether they come from the attribute_modifiers component (tools) or
    // Item#getDefaultAttributeModifiers (armor).
    @SubscribeEvent
    public static void onItemAttributeModifiers(ItemAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        ItemDarkSteelUpgrades upgrades = stack.get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null || upgrades.upgrades().isEmpty()) {
            return;
        }

        if (!(stack.getItem() instanceof DarkSteelUpgradableItem upgradableItem)
            || !upgradableItem.availableHookTypes().contains(DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES)) {
            return;
        }

        ModifyAttributesHook.modifyAttributes(upgrades, DarkSteelHelper.getHookContext(stack), event);
    }
}
