package com.enderio.enderio.api.armory.upgrades.hooks.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface GetDestroySpeedHook {
    @Nullable
    Float getDestroySpeed(DarkSteelHookContext context, BlockState state);

    static float getDestroySpeed(DarkSteelUpgradeContainer upgradeContainer, DarkSteelHookContext context, BlockState state) {
        for (GetDestroySpeedHook hook : upgradeContainer.getHooks(DarkSteelUpgradeHookTypes.GET_DESTROY_SPEED)) {
            var destroySpeed = hook.getDestroySpeed(context, state);
            if (destroySpeed != null) {
                return destroySpeed;
            }
        }

        Tool tool = context.itemStack().get(DataComponents.TOOL);
        return tool != null ? tool.getMiningSpeed(state) : 1.0F;
    }
}
