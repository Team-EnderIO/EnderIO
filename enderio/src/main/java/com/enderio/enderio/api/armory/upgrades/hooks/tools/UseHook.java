package com.enderio.enderio.api.armory.upgrades.hooks.tools;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public interface UseHook {
    InteractionResultHolder<ItemStack> use(DarkSteelHookContext context, Level level, Player player, InteractionHand usedHand, InteractionResultHolder<ItemStack> currentResult);

    static InteractionResultHolder<ItemStack> use(DarkSteelUpgradeContainer container, DarkSteelHookContext context, Level level, Player player, InteractionHand usedHand, InteractionResultHolder<ItemStack> currentResult) {
        InteractionResultHolder<ItemStack> result = currentResult;

        // TODO: Probably need a way to order/prioritise the order of hooks here - potentially for effects too
        for (UseHook hook : container.getHooks(DarkSteelUpgradeHookTypes.USE)) {
            result = hook.use(context, level, player, usedHand, result);
        }

        return result;
    }
}
