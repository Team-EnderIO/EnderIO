package com.enderio.armory.common.item.darksteel.upgrades.flight;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.lang.ArmoryLang;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.chat.Component;

public class ElytraUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "elytra";

    public ElytraUpgrade() {
    }

    @Override
    public String getSlot() {
        return GliderUpgrade.NAME;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return ArmoryLang.DS_UPGRADE_ELYTRA;
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_ELYTRA_DESCRIPTION);
    }
}
