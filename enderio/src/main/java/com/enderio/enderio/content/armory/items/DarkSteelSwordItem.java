package com.enderio.enderio.content.armory.items;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.enderio.content.armory.ArmoryTiers;
import com.enderio.enderio.content.armory.IDarkSteelItem;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class DarkSteelSwordItem extends SwordItem implements AdvancedTooltipProvider, IDarkSteelItem {
    public DarkSteelSwordItem(Properties properties) {
        super(ArmoryTiers.DARK_STEEL_TIER,
                properties.attributes(createAttributes(ArmoryTiers.DARK_STEEL_TIER, 3, -2.4F))
                        .component(EIODataComponents.TRAVEL_ITEM, false));
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}
