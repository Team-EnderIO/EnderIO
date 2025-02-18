package com.enderio.armory.api.capability;

import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

public interface IUpgradeTier {

    int getLevel();

    ModConfigSpec.ConfigValue<Integer> getActivationCost();

    Component getDisplayName();

    Supplier<IDarkSteelUpgrade> getFactory();
}
