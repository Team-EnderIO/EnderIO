package com.enderio.enderio.api.armory.upgrades.hooks.armor;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.world.entity.LivingEntity;

/**
 * Allows the wearer to walk on powdered snow, like leather boots.
 * Vanilla only checks the feet slot, so this is only called for boots.
 * @implNote This is called very often, so be sparing in the amount of work performed.
 */
public interface WalkOnPowderedSnowHook {
    boolean canWalkOnPowderedSnow(DarkSteelHookContext context, LivingEntity wearer);

    static boolean canWalkOnPowderedSnow(DarkSteelUpgradeContainer container, DarkSteelHookContext context, LivingEntity wearer) {
        for (WalkOnPowderedSnowHook hook : container.getHooks(DarkSteelUpgradeHookTypes.WALK_ON_POWDERED_SNOW)) {
            if (hook.canWalkOnPowderedSnow(context, wearer)) {
                return true;
            }
        }

        return false;
    }
}
