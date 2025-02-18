package com.enderio.armory.common.item.darksteel;

import com.enderio.armory.api.capability.IDarkSteelCapability;
import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.init.ArmoryCapabilities;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AnvilUpdateEvent;

public class AnvilRecipeHandler {

    public static void handleAnvilRecipe(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();
        IDarkSteelCapability cap = left.getCapability(ArmoryCapabilities.DARK_STEEL_CAPABILITY);
        if (cap == null) {
            return;
        }
        Item item = right.getItem();
        if (item instanceof DarkSteelUpgradeItem upItem) {
            IDarkSteelUpgrade upgrade = upItem.getUpgrade().get();
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
