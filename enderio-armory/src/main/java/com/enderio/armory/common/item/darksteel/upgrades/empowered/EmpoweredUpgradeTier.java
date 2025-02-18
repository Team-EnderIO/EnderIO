package com.enderio.armory.common.item.darksteel.upgrades.empowered;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.api.capability.IUpgradeTier;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.lang.ArmoryLang;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public enum EmpoweredUpgradeTier implements IUpgradeTier {

    ONE(ArmoryConfig.COMMON.EMPOWERED_MAX_ENERGY_I, ArmoryConfig.COMMON.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_I,
            ArmoryConfig.COMMON.EMPOWERED_ATTACK_DAMAGE_INCREASE_I,
            ArmoryConfig.COMMON.EMPOWERED_ATTACK_SPEED_INCREASE_I, ArmoryConfig.COMMON.EMPOWERED_MOB_HEAD_CHANCE_I,
            ArmoryConfig.COMMON.EMPOWERED_ACTIVATION_COST_I, ArmoryLang.DS_UPGRADE_EMPOWERED_I),
    TWO(ArmoryConfig.COMMON.EMPOWERED_MAX_ENERGY_II, ArmoryConfig.COMMON.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_II,
            ArmoryConfig.COMMON.EMPOWERED_ATTACK_DAMAGE_INCREASE_II,
            ArmoryConfig.COMMON.EMPOWERED_ATTACK_SPEED_INCREASE_II, ArmoryConfig.COMMON.EMPOWERED_MOB_HEAD_CHANCE_II,
            ArmoryConfig.COMMON.EMPOWERED_ACTIVATION_COST_II, ArmoryLang.DS_UPGRADE_EMPOWERED_II),
    THREE(ArmoryConfig.COMMON.EMPOWERED_MAX_ENERGY_III, ArmoryConfig.COMMON.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_III,
            ArmoryConfig.COMMON.EMPOWERED_ATTACK_DAMAGE_INCREASE_III,
            ArmoryConfig.COMMON.EMPOWERED_ATTACK_SPEED_INCREASE_III, ArmoryConfig.COMMON.EMPOWERED_MOB_HEAD_CHANCE_III,
            ArmoryConfig.COMMON.EMPOWERED_ACTIVATION_COST_III, ArmoryLang.DS_UPGRADE_EMPOWERED_III),
    FOUR(ArmoryConfig.COMMON.EMPOWERED_MAX_ENERGY_IV, ArmoryConfig.COMMON.EMPOWERED_DAMAGE_ABSORPTION_CHANCE_IV,
            ArmoryConfig.COMMON.EMPOWERED_ATTACK_DAMAGE_INCREASE_IV,
            ArmoryConfig.COMMON.EMPOWERED_ATTACK_SPEED_INCREASE_IV, ArmoryConfig.COMMON.EMPOWERED_MOB_HEAD_CHANCE_IV,
            ArmoryConfig.COMMON.EMPOWERED_ACTIVATION_COST_IV, ArmoryLang.DS_UPGRADE_EMPOWERED_IV);

    private final Supplier<IDarkSteelUpgrade> factory;
    private final ModConfigSpec.ConfigValue<Integer> maxStorage;
    private final ModConfigSpec.ConfigValue<Double> damageAbsorptionChance;
    private final ModConfigSpec.ConfigValue<Integer> activationCost;
    private final ModConfigSpec.ConfigValue<Integer> attackDamageIncrease;
    private final ModConfigSpec.ConfigValue<Double> attackSpeedIncrease;
    private final ModConfigSpec.ConfigValue<Double> mobHeadChance;
    private final Component displayName;

    EmpoweredUpgradeTier(ModConfigSpec.ConfigValue<Integer> maxStorage,
            ModConfigSpec.ConfigValue<Double> damageAbsorptionChance,
            ModConfigSpec.ConfigValue<Integer> attackDamageIncrease,
            ModConfigSpec.ConfigValue<Double> attackSpeedIncrease, ModConfigSpec.ConfigValue<Double> mobHeadChance,
            ModConfigSpec.ConfigValue<Integer> activationCost, Component displayName) {
        this.maxStorage = maxStorage;
        this.damageAbsorptionChance = damageAbsorptionChance;
        this.activationCost = activationCost;
        this.attackDamageIncrease = attackDamageIncrease;
        this.attackSpeedIncrease = attackSpeedIncrease;
        this.mobHeadChance = mobHeadChance;
        this.displayName = displayName;
        factory = () -> new EmpoweredUpgrade(this);
    }

    public int getMaxStorage() {
        return maxStorage.get();
    }

    public float getDamageAbsorptionChance() {
        return damageAbsorptionChance.get().floatValue();
    }

    public int getAttackDamageIncrease() {
        return attackDamageIncrease.get();
    }

    public double getAttackSpeedIncrease() {
        return attackSpeedIncrease.get();
    }

    public Supplier<IDarkSteelUpgrade> getFactory() {
        return factory;
    }

    @Override
    public int getLevel() {
        return ordinal();
    }

    public ModConfigSpec.ConfigValue<Integer> getActivationCost() {
        return activationCost;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public double getMobHeadChance() {
        return mobHeadChance.get();
    }
}
