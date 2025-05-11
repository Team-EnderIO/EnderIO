package com.enderio.armory.common.item.darksteel.upgrades.nightvision;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.lang.ArmoryLang;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.Component;

public class NightVisisionUpgrade implements IDarkSteelUpgrade {

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
