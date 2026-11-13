package com.enderio.enderio.api.armory.upgrades;

import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;

@ApiStatus.Experimental
public interface DarkSteelUpgradeContainer {
    <T> Collection<T> getHooks(DarkSteelUpgradeHookType<T> type);
}
