package com.enderio.armory.common.item.darksteel.upgrades.explosive;

import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.TieredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ExplosiveUpgrade extends TieredUpgrade<ExplosiveUpgradeTier> {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "explosive";

    public ExplosiveUpgrade() {
        this(ExplosiveUpgradeTier.ONE);
    }

    public ExplosiveUpgrade(ExplosiveUpgradeTier tier) {
        super(tier, NAME);
    }

    public int getMagnitude() {
        return tier.getMagnitude().get();
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_EXPLOSIVE_DESCRIPTION);
    }

    @Override
    protected ExplosiveUpgradeTier getBaseTier() {
        return ExplosiveUpgradeTier.ONE;
    }

    @Override
    protected Optional<ExplosiveUpgradeTier> getTier(int tier) {
        if (tier >= ExplosiveUpgradeTier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(ExplosiveUpgradeTier.values()[tier]);
    }
}
