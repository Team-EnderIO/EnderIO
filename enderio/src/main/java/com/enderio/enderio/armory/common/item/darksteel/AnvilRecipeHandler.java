package com.enderio.enderio.armory.common.item.darksteel;

import com.enderio.enderio.api.armory.capability.DarkSteelCapability;
import com.enderio.enderio.api.armory.capability.DarkSteelUpgrade;
import com.enderio.enderio.armory.common.init.ArmoryCapabilities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

public class AnvilRecipeHandler {

    public static void handleAnvilRecipe(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        DarkSteelCapability cap = left.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (cap == null) {
            return;
        }
        Item item = right.getItem();
        if (item instanceof DarkSteelUpgradeItem upItem) {
            DarkSteelUpgrade upgrade = upItem.getUpgrade().get();
            if (!cap.canApplyUpgrade(upgrade)) {
                return;
            }
            int cost = upItem.getLevelsRequired().get();

            ItemStack result = left.copy();
            cap = result.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
            if (cap == null) {
                return;
            }
            cap.addUpgrade(upgrade);
            event.setOutput(result);
            event.setCost(cost);
            event.setMaterialCost(1);
        }
    }

}
