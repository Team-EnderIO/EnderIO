package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.StepAssistUpgrade;
import com.enderio.armory.common.tag.ArmoryTags;
import net.minecraft.world.item.Item;

public class DarkSteelBootsItem extends DarkSteelArmor {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_BOOTS, EmpoweredUpgrade.NAME,
                        StepAssistUpgrade.NAME);
    }

    public DarkSteelBootsItem(Item.Properties properties) {
        super(properties, Type.BOOTS);
    }

//    @Override
//    public boolean canWalkOnPowderedSnow(@NotNull ItemStack stack, @NotNull LivingEntity wearer) {
//        return true;
//    }
}
