package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.common.capability.DarkSteelHelper;
import com.enderio.armory.common.init.ArmoryArmorMaterials;
import com.enderio.armory.common.item.darksteel.upgrades.empowered.EmpoweredUpgrade;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class DarkSteelArmor extends ArmorItem implements IDarkSteelItem {

    public DarkSteelArmor(Item.Properties properties, Type type) {
        super(ArmoryArmorMaterials.DARK_STEEL_ARMOR_MATERIAL, type, properties);
    }

    @Override
    public void setDamage(final ItemStack stack, final int newDamage) {
        super.setDamage(stack, EmpoweredUpgrade.getAdjustedDamage(stack, newDamage));
    }

    @Override
    public void addAllVariants(CreativeModeTab.Output modifier) {
        modifier.accept(this);
        modifier.accept(createFullyUpgradedStack(this));
    }

    public boolean isFoil(ItemStack pStack) {
        return DarkSteelHelper.hasUpgrade(pStack, EmpoweredUpgrade.NAME) || super.isFoil(pStack);
    }

}
