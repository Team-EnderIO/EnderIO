package com.enderio.enderio.api.armory.capability;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Optional;

// Not finished.
@ApiStatus.Internal
public interface DarkSteelCapability {

    void addUpgrade(DarkSteelUpgrade upgrade);

    void removeUpgrade(String name);

    boolean hasUpgrade(String upgrade);

    boolean canApplyUpgrade(DarkSteelUpgrade upgrade);

    Collection<DarkSteelUpgrade> getUpgrades();

    Optional<DarkSteelUpgrade> getUpgrade(String upgrade);

    <T extends DarkSteelUpgrade> Optional<T> getUpgradeAs(String upgradeName, Class<T> as);

    Collection<DarkSteelUpgrade> getUpgradesApplicable();

}
