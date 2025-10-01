package com.enderio.armory.common.item.darksteel.upgrades.flight;

import com.enderio.armory.api.capability.DarkSteelUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.lang.ArmoryLang;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

public class GliderUpgrade implements DarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "glider";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return ArmoryLang.DS_UPGRADE_GLIDER;
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_GLIDER_DESCRIPTION);
    }

}
