package com.enderio.api.integration;

import com.enderio.api.glider.GliderMovementInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Optional;

/**
 * These are all the methods a Integration can override or call.
 * Please make sure that all methods only reference API of the integrated mod or Minecraft classes, so that this can be part of the API, after stable release
 */
public interface Integration {

    default void addEventListener(IEventBus modEventBus, IEventBus forgeEventBus) {
    }

    /**
     * @param stack The ItemStack used to mine the block
     * @return if this ItemStack can mine blocks directly
     */
    default boolean canMineWithDirect(ItemStack stack) {
        return false;
    }

    /**
     * @param player
     * @return all the GliderMovementInfo this player currently has, or Optional#empty if the player isn't allowed to glide
     */
    default Optional<GliderMovementInfo> getGliderMovementInfo(Player player) {
        return Optional.empty();
    }

    /**
     * @param player The Player who want to activate the HangGlider
     * @return if the HangGlider should be disabled, because of a no fly zone or something else
     */
    default boolean isHangGliderDisabled(Player player) {
        return false;
    }

    /**
     * @return a ClientIntegration used for client only stuff like rendering
     */
    default ClientIntegration getClientIntegration() {
        return ClientIntegration.NOOP;
    }
}