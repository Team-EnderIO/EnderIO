package com.enderio.base.common.item.darksteel.upgrades.explosive;

import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.item.darksteel.upgrades.IUpgradeTier;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

import static com.enderio.base.common.lang.EIOLang.DS_UPGRADE_EXPLOSIVE_PENETRATION_I;
import static com.enderio.base.common.lang.EIOLang.DS_UPGRADE_EXPLOSIVE_PENETRATION_II;

public enum ExplosivePenetrationUpgradeTier implements IUpgradeTier {

    ONE(BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_PENETRATION_I, BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_PENETRATION_ACTIVATION_COST_I,
        DS_UPGRADE_EXPLOSIVE_PENETRATION_I),
    TWO(BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_PENETRATION_II, BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_PENETRATION_ACTIVATION_COST_II,
        DS_UPGRADE_EXPLOSIVE_PENETRATION_II);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ForgeConfigSpec.ConfigValue<Integer> magnitude;
    private final ForgeConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;

    ExplosivePenetrationUpgradeTier(ForgeConfigSpec.ConfigValue<Integer> magnitude, ForgeConfigSpec.ConfigValue<Integer> activationCost, Component displayName) {
        this.magnitude = magnitude;
        this.activationCost = activationCost;
        this.displayName = displayName;
        factory = () -> new ExplosivePenetrationUpgrade(this);
    }

    public ForgeConfigSpec.ConfigValue<Integer> getMagnitude() {
        return magnitude;
    }

    public Supplier<IDarkSteelUpgrade> getFactory() {
        return factory;
    }

    public ForgeConfigSpec.ConfigValue<Integer> getActivationCost() {
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
