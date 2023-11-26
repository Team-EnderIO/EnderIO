package com.enderio.armory.common.item.darksteel.upgrades.explosive;

import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.item.darksteel.upgrades.IUpgradeTier;
import com.enderio.armory.common.lang.ArmoryLang;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public enum ExplosiveUpgradeTier implements IUpgradeTier {

    ONE(ArmoryConfig.COMMON.EXPLOSIVE_I,
        ArmoryConfig.COMMON.EXPLOSIVE_RADIUS_ACTIVATION_COST_I,
        ArmoryLang.DS_UPGRADE_EXPLOSIVE_I),
    TWO(ArmoryConfig.COMMON.EXPLOSIVE_II,
        ArmoryConfig.COMMON.EXPLOSIVE_ACTIVATION_COST_II,
        ArmoryLang.DS_UPGRADE_EXPLOSIVE_II);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ForgeConfigSpec.ConfigValue<Integer> magnitude;
    private final ForgeConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;

    ExplosiveUpgradeTier(ForgeConfigSpec.ConfigValue<Integer> magnitude, ForgeConfigSpec.ConfigValue<Integer> activationCost,
        Component displayName) {
        this.magnitude = magnitude;
        this.activationCost = activationCost;
        this.displayName = displayName;
        factory = () -> new ExplosiveUpgrade(this);
    }

    public ForgeConfigSpec.ConfigValue<Integer> getMagnitude() {
        return magnitude;
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

    public ForgeConfigSpec.ConfigValue<Integer> getActivationCost() {
        return activationCost;
    }
}
