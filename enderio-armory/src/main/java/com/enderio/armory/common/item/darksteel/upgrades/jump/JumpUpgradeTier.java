package com.enderio.armory.common.item.darksteel.upgrades.jump;

import com.enderio.armory.api.capability.DarkSteelUpgrade;
import com.enderio.armory.api.capability.UpgradeTier;
import com.enderio.armory.common.config.ArmoryConfig;
import com.enderio.armory.common.lang.ArmoryLang;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

public enum JumpUpgradeTier implements UpgradeTier {
    ONE(ArmoryConfig.COMMON.JUMP_COUNT_I, ArmoryConfig.COMMON.JUMP_ACTIVATION_COST_I, ArmoryLang.DS_UPGRADE_JUMP_I,
            ArmoryLang.DS_UPGRADE_JUMP_I_DESCRIPTION),
    TWO(ArmoryConfig.COMMON.JUMP_COUNT_II, ArmoryConfig.COMMON.JUMP_ACTIVATION_COST_II, ArmoryLang.DS_UPGRADE_JUMP_II,
            ArmoryLang.DS_UPGRADE_JUMP_II_DESCRIPTION);

    private final Supplier<DarkSteelUpgrade> factory;
    private final ModConfigSpec.ConfigValue<Integer> numJumps;
    private final ModConfigSpec.ConfigValue<Integer> activationCost;
    private final Component displayName;
    private final Component description;

    JumpUpgradeTier(ModConfigSpec.ConfigValue<Integer> numJumps, ModConfigSpec.ConfigValue<Integer> activationCost,
            Component displayName, Component description) {
        this.numJumps = numJumps;
        this.activationCost = activationCost;
        this.displayName = displayName;
        this.description = description;
        factory = () -> new JumpUpgrade(this);
    }

    public ModConfigSpec.ConfigValue<Integer> getNumJumps() {
        return numJumps;
    }

    @Override
    public Supplier<DarkSteelUpgrade> getFactory() {
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

    public Component getDescription() {
        return description;
    }
}
