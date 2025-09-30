package com.enderio.enderio.api.armory.capability;

import java.util.Collection;
import java.util.Optional;

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
