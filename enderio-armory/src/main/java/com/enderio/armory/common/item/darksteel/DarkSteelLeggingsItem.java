package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.speed.SpeedUpgrade;
import com.enderio.armory.common.tag.ArmoryTags;
import net.minecraft.world.item.Item;

public class DarkSteelLeggingsItem extends DarkSteelArmor {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_LEGGINGS, EmpoweredUpgrade.NAME,
                        SpeedUpgrade.NAME);
    }

    public DarkSteelLeggingsItem(Item.Properties properties) {
        super(properties, Type.LEGGINGS);
    }

}
