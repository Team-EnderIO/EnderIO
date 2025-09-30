package com.enderio.enderio.api.armory.capability;

import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.ApiStatus;

// Not finished.
@ApiStatus.Internal
public interface UpgradeTier {

    int getLevel();

    ModConfigSpec.ConfigValue<Integer> getActivationCost();

    Component getDisplayName();

    Supplier<DarkSteelUpgrade> getFactory();
}
