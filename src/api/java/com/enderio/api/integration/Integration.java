package com.enderio.api.integration;

import com.enderio.api.glider.GliderMovementInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.data.event.GatherDataEvent;
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
     * @param player The Player who activates the HangGlider
     *               used for cunsuming energy or other things
     */
    default void onHangGliderTick(Player player) {
    }

    /**
     * @param player The Player who want to activate the HangGlider
     * @return if the HangGlider should be disabled return the Reason, if not, return an empty Optional
     */
    default Optional<Component> hangGliderDisabledReason(Player player) {
        return Optional.empty();
    }

    /**
     * @return a ClientIntegration used for client only stuff like rendering
     */
    default ClientIntegration getClientIntegration() {
        return ClientIntegration.NOOP;
    }

    default void createData(GatherDataEvent event) {
    }

    default void onModConstruct() {

    }

    /**
     * @param stack The ItemStack a conduit was rightclicked with
     * @return empty Optional if this stack is not a facade item. Or the BlockState this facade disguises as
     */
    default Optional<BlockState> getFacadeOf(ItemStack stack) {
        return Optional.empty();
    }
}