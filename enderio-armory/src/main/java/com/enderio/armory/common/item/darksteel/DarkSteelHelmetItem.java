package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.nightvision.NightVisisionUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.solar.SolarUpgrade;
import com.enderio.armory.common.tag.ArmoryTags;
import net.minecraft.world.item.Item;

public class DarkSteelHelmetItem extends DarkSteelArmor {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_HELMET, EmpoweredUpgrade.NAME,
                        NightVisisionUpgrade.NAME, SolarUpgrade.NAME);
    }

    public DarkSteelHelmetItem(Item.Properties properties) {
        super(properties, Type.HELMET);
    }

}
