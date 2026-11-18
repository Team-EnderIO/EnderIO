package com.enderio.enderio.api.armory.upgrades.hooks.armor;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.world.entity.LivingEntity;

/**
 * Determines whether the wearing entity can be frozen by powdered snow.
 * Any single hook returning false prevents freezing.
 * @implNote This is called every tick, so be sparing in the amount of work performed.
 */
public interface CanFreezeHook {
    /**
     * @return false to prevent the entity from freezing.
     */
    boolean canFreeze(DarkSteelHookContext context, LivingEntity entity);

    static boolean canFreeze(DarkSteelUpgradeContainer container, DarkSteelHookContext context, LivingEntity entity) {
        for (CanFreezeHook hook : container.getHooks(DarkSteelUpgradeHookTypes.CAN_FREEZE)) {
            if (!hook.canFreeze(context, entity)) {
                return false;
            }
        }

        return true;
    }
}
