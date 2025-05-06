package com.enderio.armory.common.item.darksteel.upgrades.solar;

import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.TieredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;

public class SolarUpgrade extends TieredUpgrade<SolarUpgradeTier> {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "solar";

    public SolarUpgrade() {
        this(SolarUpgradeTier.ONE);
    }

    public SolarUpgrade(SolarUpgradeTier tier) {
        super(tier, NAME);
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_SOLAR_DESCRIPTION);
    }

    public SolarPanelTier getPanelTier() {
        return tier.getPanelTier();
    }

    @Override
    protected SolarUpgradeTier getBaseTier() {
        return SolarUpgradeTier.ONE;
    }

    @Override
    protected Optional<SolarUpgradeTier> getTier(int tier) {
        if (tier >= SolarUpgradeTier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(SolarUpgradeTier.values()[tier]);
    }
}
