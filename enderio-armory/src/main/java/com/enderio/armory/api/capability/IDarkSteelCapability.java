package com.enderio.armory.api.capability;

import java.util.Collection;
import java.util.Optional;

public interface IDarkSteelCapability {
    void addUpgrade(IDarkSteelUpgrade upgrade);

    void removeUpgrade(String name);

    boolean canApplyUpgrade(IDarkSteelUpgrade upgrade);

    <T extends IDarkSteelUpgrade> Optional<T> getUpgradeAs(String upgradeName, Class<T> as);

    Optional<IDarkSteelUpgrade> getUpgrade(String upgrade);

    Collection<IDarkSteelUpgrade> getUpgrades();

    boolean hasUpgrade(String upgrade);

    Collection<IDarkSteelUpgrade> getUpgradesApplicable();
}
