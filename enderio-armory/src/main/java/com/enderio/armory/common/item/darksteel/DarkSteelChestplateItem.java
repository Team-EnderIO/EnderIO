package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.glider.GliderUpgrade;
import com.enderio.armory.common.tag.ArmoryTags;

public class DarkSteelChestplateItem extends DarkSteelArmor {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_CHESTPLATE, EmpoweredUpgrade.NAME,
                        GliderUpgrade.NAME);
    }

    public DarkSteelChestplateItem(Properties properties) {
        super(properties, Type.CHESTPLATE);
    }

}
