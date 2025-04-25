package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.item.darksteel.upgrades.DarkSteelUpgradeRegistry;
import com.enderio.armory.common.item.darksteel.upgrades.StepAssistUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import com.enderio.armory.common.item.darksteel.upgrades.jump.JumpUpgrade;
import com.enderio.armory.common.lang.ArmoryLang;
import com.enderio.armory.common.tag.ArmoryTags;
import com.enderio.core.common.util.TooltipUtil;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DarkSteelBootsItem extends DarkSteelArmor {

    static {
        DarkSteelUpgradeRegistry.instance()
                .registerUpgradesForItem(ArmoryTags.Items.DARK_STEEL_UPGRADEABLE_BOOTS, EmpoweredUpgrade.NAME,
                        StepAssistUpgrade.NAME, JumpUpgrade.NAME);
    }

    public DarkSteelBootsItem(Item.Properties properties) {
        super(properties, Type.BOOTS);
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
        return DarkSteelHelper.hasUpgrade(stack, EmpoweredUpgrade.NAME);
    }

    @Override
    public void addCurrentUpgradeTooltips(ItemStack itemStack, List<Component> tooltips, boolean isDetailed) {
        super.addCurrentUpgradeTooltips(itemStack, tooltips, isDetailed);
        if (isDetailed && getEmpoweredUpgrade(itemStack).isPresent()) {
            tooltips.add(TooltipUtil.style(ArmoryLang.DS_UPGRADE_BOOTS_SNOW_DESCRIPTION));
        }
    }
}
