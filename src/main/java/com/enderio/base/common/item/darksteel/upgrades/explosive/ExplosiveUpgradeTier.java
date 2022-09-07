package com.enderio.base.common.item.darksteel.upgrades.explosive;

import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.item.darksteel.upgrades.IUpgradeTier;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

import static com.enderio.base.common.lang.EIOLang.DS_UPGRADE_EXPLOSIVE_I;
import static com.enderio.base.common.lang.EIOLang.DS_UPGRADE_EXPLOSIVE_II;

public enum ExplosiveUpgradeTier implements IUpgradeTier {

    ONE(BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_I,
        BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_RADIUS_ACTIVATION_COST_I,
        DS_UPGRADE_EXPLOSIVE_I),
    TWO(BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_II,
        BaseConfig.COMMON.DARK_STEEL.EXPLOSIVE_ACTIVATION_COST_II,
        DS_UPGRADE_EXPLOSIVE_II);

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
