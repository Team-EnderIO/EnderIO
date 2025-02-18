package com.enderio.armory.api.capability;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

public interface IUpgradeTier {

    int getLevel();

    ModConfigSpec.ConfigValue<Integer> getActivationCost();

    Component getDisplayName();

    Supplier<IDarkSteelUpgrade> getFactory();
}
