package com.enderio.armory.common.item.darksteel.upgrades.explosive;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.api.capability.IUpgradeTier;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.lang.ArmoryLang;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum ExplosivePenetrationUpgradeTier implements IUpgradeTier {

    ONE(ArmoryConfig.COMMON.EXPLOSIVE_PENETRATION_I, ArmoryConfig.COMMON.EXPLOSIVE_PENETRATION_ACTIVATION_COST_I,
            ArmoryLang.DS_UPGRADE_EXPLOSIVE_PENETRATION_I),
    TWO(ArmoryConfig.COMMON.EXPLOSIVE_PENETRATION_II, ArmoryConfig.COMMON.EXPLOSIVE_PENETRATION_ACTIVATION_COST_II,
            ArmoryLang.DS_UPGRADE_EXPLOSIVE_PENETRATION_II);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ModConfigSpec.ConfigValue<Integer> magnitude;
    private final ModConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;

    ExplosivePenetrationUpgradeTier(ModConfigSpec.ConfigValue<Integer> magnitude,
            ModConfigSpec.ConfigValue<Integer> activationCost, Component displayName) {
        this.magnitude = magnitude;
        this.activationCost = activationCost;
        this.displayName = displayName;
        factory = () -> new ExplosivePenetrationUpgrade(this);
    }

    public ModConfigSpec.ConfigValue<Integer> getMagnitude() {
        return magnitude;
    }

    public Supplier<IDarkSteelUpgrade> getFactory() {
        return factory;
    }

    public ModConfigSpec.ConfigValue<Integer> getActivationCost() {
        return activationCost;
    }

    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public int getLevel() {
        return ordinal();
    }
}
