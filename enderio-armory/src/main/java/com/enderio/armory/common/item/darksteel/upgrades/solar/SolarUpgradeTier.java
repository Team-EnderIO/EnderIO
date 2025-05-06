package com.enderio.armory.common.item.darksteel.upgrades.solar;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.api.capability.IUpgradeTier;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.machines.common.blockentity.solar.SolarPanelTier;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum SolarUpgradeTier implements IUpgradeTier {

    ONE(ArmoryConfig.COMMON.SOLAR_ACTIVATION_COST_I, ArmoryLang.DS_UPGRADE_SOLAR_I, SolarPanelTier.ENERGETIC),
    TWO(ArmoryConfig.COMMON.SOLAR_ACTIVATION_COST_II, ArmoryLang.DS_UPGRADE_SOLAR_II, SolarPanelTier.PULSATING),
    THREE(ArmoryConfig.COMMON.SOLAR_ACTIVATION_COST_III, ArmoryLang.DS_UPGRADE_SOLAR_III, SolarPanelTier.VIBRANT);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ModConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;

    private final SolarPanelTier panelTier;

    SolarUpgradeTier(ModConfigSpec.ConfigValue<Integer> activationCost, Component displayName,
            SolarPanelTier panelTier) {
        this.activationCost = activationCost;
        this.displayName = displayName;
        this.panelTier = panelTier;
        factory = () -> new SolarUpgrade(this);
    }

    @Override
    public Supplier<IDarkSteelUpgrade> getFactory() {
        return factory;
    }

    @Override
    public int getLevel() {
        return ordinal();
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public ModConfigSpec.ConfigValue<Integer> getActivationCost() {
        return activationCost;
    }

    public SolarPanelTier getPanelTier() {
        return panelTier;
    }
}
