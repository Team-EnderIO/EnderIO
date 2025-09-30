package com.enderio.enderio.armory.common.item.darksteel.upgrades.direct;

import com.enderio.enderio.api.armory.capability.DarkSteelUpgrade;
import com.enderio.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.enderio.armory.common.lang.ArmoryLang;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

public class DirectUpgrade implements DarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "direct";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return ArmoryLang.DS_UPGRADE_DIRECT;
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_DIRECT_DESCRIPTION);
    }
}
