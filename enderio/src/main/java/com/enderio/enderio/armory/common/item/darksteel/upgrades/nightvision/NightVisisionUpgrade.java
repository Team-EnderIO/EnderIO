package com.enderio.enderio.armory.common.item.darksteel.upgrades.nightvision;

import com.enderio.enderio.api.armory.capability.DarkSteelUpgrade;
import com.enderio.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.enderio.armory.common.lang.ArmoryLang;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

public class NightVisisionUpgrade implements DarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "nightVision";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return ArmoryLang.DS_UPGRADE_NIGHT_VISION;
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_NIGHT_VISION_DESCRIPTION);
    }

}
