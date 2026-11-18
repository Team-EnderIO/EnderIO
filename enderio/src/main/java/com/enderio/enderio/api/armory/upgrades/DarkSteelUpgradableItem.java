package com.enderio.enderio.api.armory.upgrades;

import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;

import java.util.Set;

public interface DarkSteelUpgradableItem {
    /**
     * Used for checking compatibility with incoming upgrades.
     * If an upgrade's effects require a hook that isn't in this list, it cannot be used.
     * @return the set of available hooks.
     */
    Set<DarkSteelUpgradeHookType<?>> availableHookTypes();
}
