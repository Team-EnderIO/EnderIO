package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.api.capability.IDarkSteelUpgrade;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ForgeConfigSpec;

import java.util.function.Supplier;

public interface IUpgradeTier {

    int getLevel();

    ForgeConfigSpec.ConfigValue<Integer> getActivationCost();

    Component getDisplayName();

    Supplier<IDarkSteelUpgrade> getFactory();

}
