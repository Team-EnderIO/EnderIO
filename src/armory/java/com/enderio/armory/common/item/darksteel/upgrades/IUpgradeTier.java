package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.api.capability.IDarkSteelUpgrade;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.function.Supplier;

public interface IUpgradeTier {

    int getLevel();

    ModConfigSpec.ConfigValue<Integer> getActivationCost();

    Component getDisplayName();

    Supplier<IDarkSteelUpgrade> getFactory();

}
