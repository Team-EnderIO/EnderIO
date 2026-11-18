package com.enderio.enderio.api.armory.upgrades.hooks.armor;

import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelHookContext;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookTypes;
import net.minecraft.world.entity.LivingEntity;

public interface ElytraFlyHook {
    /**
     * Whether this hook allows the entity to start (or keep) elytra flying.
     */
    boolean canElytraFly(DarkSteelHookContext context, LivingEntity entity);

    /**
     * Called every tick while flying, on both sides. Only called if {@link #canElytraFly} returned true.
     * @return whether flight should continue.
     */
    default boolean elytraFlightTick(DarkSteelHookContext context, LivingEntity entity, int flightTicks) {
        return true;
    }

    static boolean canElytraFly(DarkSteelUpgradeContainer container, DarkSteelHookContext context, LivingEntity entity) {
        for (ElytraFlyHook hook : container.getHooks(DarkSteelUpgradeHookTypes.ELYTRA_FLY)) {
            if (hook.canElytraFly(context, entity)) {
                return true;
            }
        }

        return false;
    }

    static boolean elytraFlightTick(DarkSteelUpgradeContainer container, DarkSteelHookContext context, LivingEntity entity, int flightTicks) {
        for (ElytraFlyHook hook : container.getHooks(DarkSteelUpgradeHookTypes.ELYTRA_FLY)) {
            if (hook.canElytraFly(context, entity) && hook.elytraFlightTick(context, entity, flightTicks)) {
                return true;
            }
        }

        return false;
    }
}
