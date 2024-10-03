package com.enderio.core.client.item;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface AdvancedTooltipProvider {

    /**
     * Add tooltips that are always displayed
     * @param itemStack shown for this item
     * @param player the player
     * @param tooltips add tooltips to this list
     */
    default void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
    }

    /**
     * Tooltips shown when shift is hot held
     * @param itemStack shown for this item
     * @param player the player
     * @param tooltips add tooltips to this list
     */
    default void addBasicTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
    }

    /**
     * Add tooltips that are displayed when shift is held
     * @param itemStack shown for this item
     * @param player the player
     * @param tooltips add tooltips to this list
     */
    default void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
    }

}
