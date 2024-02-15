package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.lang.ArmoryLang;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public enum EmpoweredUpgradeTier implements IUpgradeTier {

    ONE(ArmoryConfig.COMMON.EMPOWERED_MAX_ENERGY_I,
        ArmoryConfig.COMMON.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_I,
        ArmoryConfig.COMMON.EMPOWERED_ACTIVATION_COST_I,
        ArmoryLang.DS_UPGRADE_EMPOWERED_I),
    TWO(ArmoryConfig.COMMON.EMPOWERED_MAX_ENERGY_II,
        ArmoryConfig.COMMON.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_II,
        ArmoryConfig.COMMON.EMPOWERED_ACTIVATION_COST_II,
        ArmoryLang.DS_UPGRADE_EMPOWERED_II),
    THREE(ArmoryConfig.COMMON.EMPOWERED_MAX_ENERGY_III,
        ArmoryConfig.COMMON.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_III,
        ArmoryConfig.COMMON.EMPOWERED_ACTIVATION_COST_III,
        ArmoryLang.DS_UPGRADE_EMPOWERED_III),
    FOUR(ArmoryConfig.COMMON.EMPOWERED_MAX_ENERGY_IV,
        ArmoryConfig.COMMON.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_IV,
        ArmoryConfig.COMMON.EMPOWERED_ACTIVATION_COST_IV,
        ArmoryLang.DS_UPGRADE_EMPOWERED_IV);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ForgeConfigSpec.ConfigValue<Integer> maxStorage;
    private final ForgeConfigSpec.ConfigValue<Double> damageAbsorptionChance;
    private final ForgeConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;

    EmpoweredUpgradeTier(ForgeConfigSpec.ConfigValue<Integer> maxStorage, ForgeConfigSpec.ConfigValue<Double> damageAbsorptionChance,
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
        return damageAbsorptionChance.get().floatValue();
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
