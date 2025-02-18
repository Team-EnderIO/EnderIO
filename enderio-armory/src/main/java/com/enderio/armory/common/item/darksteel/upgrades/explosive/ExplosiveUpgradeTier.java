package com.enderio.armory.common.item.darksteel.upgrades.explosive;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.api.capability.IUpgradeTier;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.lang.ArmoryLang;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum ExplosiveUpgradeTier implements IUpgradeTier {

    ONE(ArmoryConfig.COMMON.EXPLOSIVE_I_RANGE, ArmoryConfig.COMMON.EXPLOSIVE_RADIUS_ACTIVATION_COST_I,
            ArmoryLang.DS_UPGRADE_EXPLOSIVE_I),
    TWO(ArmoryConfig.COMMON.EXPLOSIVE_II_RANGE, ArmoryConfig.COMMON.EXPLOSIVE_ACTIVATION_COST_II,
            ArmoryLang.DS_UPGRADE_EXPLOSIVE_II);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ModConfigSpec.ConfigValue<Integer> magnitude;
    private final ModConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;

    ExplosiveUpgradeTier(ModConfigSpec.ConfigValue<Integer> magnitude,
            ModConfigSpec.ConfigValue<Integer> activationCost, Component displayName) {
        this.magnitude = magnitude;
        this.activationCost = activationCost;
        this.displayName = displayName;
        factory = () -> new ExplosiveUpgrade(this);
    }

    public ModConfigSpec.ConfigValue<Integer> getMagnitude() {
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

    public ModConfigSpec.ConfigValue<Integer> getActivationCost() {
        return activationCost;
    }
}
