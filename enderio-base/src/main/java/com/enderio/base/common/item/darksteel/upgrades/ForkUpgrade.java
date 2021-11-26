package com.enderio.base.common.item.darksteel.upgrades;

import com.enderio.base.common.capability.darksteel.IDarkSteelUpgrade;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.List;

public class ForkUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "fork";

    public static ForkUpgrade create() {
        return new ForkUpgrade();
    }

    @Override
    public String getSerializedName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return EIOLang.DS_UPGRADE_FORK;
    }

    @Override
    public Collection<Component> getDescription() { return List.of(EIOLang.DS_UPGRADE_FORK_DESCRIPTION); }
}
