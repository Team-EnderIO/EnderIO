package com.enderio.enderio.api.armory.upgrades.hooks.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IsCorrectToolForDropsHook {
    @Nullable
    Boolean isCorrectToolForDrops(DarkSteelHookContext context, BlockState state);

    static boolean isCorrectToolForDrops(DarkSteelUpgradeContainer upgradeContainer, DarkSteelHookContext context, BlockState state) {
        for (IsCorrectToolForDropsHook hook : upgradeContainer.getHooks(DarkSteelUpgradeHookTypes.IS_CORRECT_TOOL_FOR_DROPS)) {
            var isCorrectTool = hook.isCorrectToolForDrops(context, state);

            // If it's null or false, keep searching
            if (isCorrectTool != null && isCorrectTool) {
                return true;
            }
        }

        Tool tool = context.itemStack().get(DataComponents.TOOL);
        return tool != null && tool.isCorrectForDrops(state);
    }
}
