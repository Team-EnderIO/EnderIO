package com.enderio.enderio.api.armory.upgrades.hooks.general;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

/**
 * Adjusts the attribute modifiers an item provides while equipped.
 * @implNote The modifiers can only depend on item state, not other external state like the level.
 * @implNote This can be called very often, so be sparing in the amount of work performed.
 */
public interface ModifyAttributesHook {
    /**
     * @param slotGroup the slots the item's upgrades are active in.
     */
    void modifyAttributes(DarkSteelHookContext context, ItemAttributeModifierEvent event);

    static void modifyAttributes(DarkSteelUpgradeContainer container, DarkSteelHookContext context, ItemAttributeModifierEvent event) {
        for (ModifyAttributesHook hook : container.getHooks(DarkSteelUpgradeHookTypes.MODIFY_ATTRIBUTES)) {
            hook.modifyAttributes(context, event);
        }
    }
}
