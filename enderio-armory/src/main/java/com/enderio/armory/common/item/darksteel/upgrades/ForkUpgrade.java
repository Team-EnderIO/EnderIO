package com.enderio.armory.common.item.darksteel.upgrades;

import com.enderio.armory.api.capability.IDarkSteelUpgrade;
import com.enderio.armory.common.init.ArmoryItems;
import com.enderio.armory.common.lang.ArmoryLang;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;

public class ForkUpgrade implements IDarkSteelUpgrade {

    public static final String NAME = DarkSteelUpgradeRegistry.UPGRADE_PREFIX + "fork";

    public ForkUpgrade() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Component getDisplayName() {
        return ArmoryLang.DS_UPGRADE_FORK;
    }

    @Override
    public Collection<Component> getDescription() {
        return List.of(ArmoryLang.DS_UPGRADE_FORK_DESCRIPTION);
    }

    public void onAddedToItem(ItemStack stack) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) {
            tool = ArmoryItems.DARK_STEEL_TIER.createToolProperties(BlockTags.MINEABLE_WITH_HOE);
            stack.set(DataComponents.TOOL, tool);
        } else {
            List<Tool.Rule> newRules = new ArrayList<>(tool.rules());
            newRules.add(Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_HOE, ArmoryItems.DARK_STEEL_TIER.getSpeed()));
            stack.set(DataComponents.TOOL, new Tool(newRules, tool.defaultMiningSpeed(), tool.damagePerBlock()));
        }
    }

    public void onRemovedFromItem(ItemStack stack) {
        Tool tool = stack.get(DataComponents.TOOL);
        if (tool == null) {
            return;
        }
        Tool.Rule toRemove = Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_HOE,
                ArmoryItems.DARK_STEEL_TIER.getSpeed());
        List<Tool.Rule> newRules = new ArrayList<>(tool.rules());
        newRules.remove(toRemove);
        stack.set(DataComponents.TOOL, new Tool(newRules, tool.defaultMiningSpeed(), tool.damagePerBlock()));
    }

}
