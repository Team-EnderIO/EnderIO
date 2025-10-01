package com.enderio.armory.common.item.darksteel.upgrades.explosive;

import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.TieredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ExplosivePenetrationUpgrade extends TieredUpgrade<ExplosivePenetrationUpgradeTier> {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "explosive_penetration";

    public ExplosivePenetrationUpgrade() {
        this(ExplosivePenetrationUpgradeTier.ONE);
    }

    public ExplosivePenetrationUpgrade(ExplosivePenetrationUpgradeTier tier) {
        super(tier, NAME);
    }

    public int getMagnitude() {
        return tier.getMagnitude().get();
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_EXPLOSIVE_PENETRATION_DESCRIPTION);
    }

    @Override
    protected ExplosivePenetrationUpgradeTier getBaseTier() {
        return ExplosivePenetrationUpgradeTier.ONE;
    }

    @Override
    protected Optional<ExplosivePenetrationUpgradeTier> getTier(int tier) {
        if (tier >= ExplosivePenetrationUpgradeTier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(ExplosivePenetrationUpgradeTier.values()[tier]);
    }

}
