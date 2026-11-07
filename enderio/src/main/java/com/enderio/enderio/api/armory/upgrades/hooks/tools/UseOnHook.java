package com.enderio.enderio.api.armory.upgrades.hooks.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public interface UseOnHook {
    InteractionResult useOn(DarkSteelHookContext context, UseOnContext useOnContext, InteractionResult currentResult);

    static InteractionResult useOn(DarkSteelUpgradeContainer container, DarkSteelHookContext context, UseOnContext useOnContext, InteractionResult currentResult) {
        InteractionResult result = currentResult;

        // TODO: Probably need a way to order/prioritise the order of hooks here - potentially for effects too
        for (UseOnHook hook : container.getHooks(DarkSteelUpgradeHookTypes.USE_ON)) {
            result = hook.useOn(context, useOnContext, result);
        }

        return result;
    }
}
