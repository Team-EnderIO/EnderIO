package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public enum EmpoweredUpgradeTier implements IUpgradeTier {

    ONE(BaseConfig.COMMON.DARK_STEEL.EMPOWERED_MAX_ENERGY_I,
        BaseConfig.COMMON.DARK_STEEL.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_I,
        BaseConfig.COMMON.DARK_STEEL.EMPOWERED_ACTIVATION_COST_I,
        EIOLang.DS_UPGRADE_EMPOWERED_I),
    TWO(BaseConfig.COMMON.DARK_STEEL.EMPOWERED_MAX_ENERGY_II,
        BaseConfig.COMMON.DARK_STEEL.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_II,
        BaseConfig.COMMON.DARK_STEEL.EMPOWERED_ACTIVATION_COST_II,
        EIOLang.DS_UPGRADE_EMPOWERED_II),
    THREE(BaseConfig.COMMON.DARK_STEEL.EMPOWERED_MAX_ENERGY_III,
        BaseConfig.COMMON.DARK_STEEL.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_III,
        BaseConfig.COMMON.DARK_STEEL.EMPOWERED_ACTIVATION_COST_III,
        EIOLang.DS_UPGRADE_EMPOWERED_III),
    FOUR(BaseConfig.COMMON.DARK_STEEL.EMPOWERED_MAX_ENERGY_IV,
        BaseConfig.COMMON.DARK_STEEL.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_IV,
        BaseConfig.COMMON.DARK_STEEL.EMPOWERED_ACTIVATION_COST_IV,
        EIOLang.DS_UPGRADE_EMPOWERED_IV);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ForgeConfigSpec.ConfigValue<Integer> maxStorage;
    private final ForgeConfigSpec.ConfigValue<Float> damageAbsorptionChance;
    private final ForgeConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;

    EmpoweredUpgradeTier(ForgeConfigSpec.ConfigValue<Integer> maxStorage, ForgeConfigSpec.ConfigValue<Float> damageAbsorptionChance,
        ForgeConfigSpec.ConfigValue<Integer> activationCost, Component displayName) {
        this.maxStorage = maxStorage;
        this.damageAbsorptionChance = damageAbsorptionChance;
        this.activationCost = activationCost;
        this.displayName = displayName;
        factory = () -> new EmpoweredUpgrade(this);
    }

    public int getMaxStorage() {
        return maxStorage.get();
    }

    public float getDamageAbsorptionChance() {
        return damageAbsorptionChance.get();
    }

    public Supplier<IDarkSteelUpgrade> getFactory() {
        return factory;
    }

    @Override
    public int getLevel() {
        return ordinal();
    }

    public ForgeConfigSpec.ConfigValue<Integer> getActivationCost() {
        return activationCost;
    }

    public Component getDisplayName() {
        return displayName;
    }
}
