package com.enderio.armory.common.item.darksteel.upgrades.speed;

import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.TieredUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;

public class SpeedUpgrade extends TieredUpgrade<SpeedUpgradeTier> {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "explosive";

    public SpeedUpgrade() {
        this(SpeedUpgradeTier.ONE);
    }

    public SpeedUpgrade(SpeedUpgradeTier tier) {
        super(tier, NAME);
    }

    public double getMagnitude() {
        return tier.getMagnitude().get();
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_SPEED_DESCRIPTION);
    }

    @Override
    protected SpeedUpgradeTier getBaseTier() {
        return SpeedUpgradeTier.ONE;
    }

    @Override
    protected Optional<SpeedUpgradeTier> getTier(int tier) {
        if (tier >= SpeedUpgradeTier.values().length || tier < 0) {
            return Optional.empty();
        }
        return Optional.of(SpeedUpgradeTier.values()[tier]);
    }

}
