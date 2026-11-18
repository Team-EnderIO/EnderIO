package com.enderio.enderio.api.armory.upgrades.hooks.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.neoforged.neoforge.common.ItemAbility;

public interface CanPerformActionHook {
    boolean canPerformAction(DarkSteelHookContext context, ItemAbility itemAbility);

    static boolean canPerformAction(DarkSteelUpgradeContainer upgradeContainer, DarkSteelHookContext context, ItemAbility itemAbility) {
        return upgradeContainer.getHooks(DarkSteelUpgradeHookTypes.CAN_PERFORM_ACTION)
            .stream()
            .anyMatch(hook -> hook.canPerformAction(context, itemAbility));
    }
}
