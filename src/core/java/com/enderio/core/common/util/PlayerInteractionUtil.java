package com.enderio.core.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class PlayerInteractionUtil {
    /**
     * Tries to transfer the result of a world interaction into the player's inventory, and if not successful, drops them on the ground where the event took place.
     *
     * @param player - the player that performed the event.
     * @param interactionPosition - Where the interaction took place, i.e. where the player clicked, the block was etc.
     * @param drops - The list of drops that comes from the event.
     */
    public static void putStacksInInventoryFromWorldInteraction(Player player, BlockPos interactionPosition, List<ItemStack> drops){
        for (ItemStack drop: drops) {
            putItemInInventoryFromWorldInteraction(player, interactionPosition, drop);
        }
    }

    /**
     * Tries to transfer a single ItemStack, which was the result of a world interaction, into the player's inventory, and if not successful,
     * drops them on the ground where the event took place.
     *
     * @param player - the player that performed the event.
     * @param interactionPosition - Where the interaction took place, i.e. where the player clicked, the block was etc.
     * @param item - The ItemStack that comes from the event.
     */
    public static void putItemInInventoryFromWorldInteraction(Player player, BlockPos interactionPosition, ItemStack item){
        Level level = player.level();
        if(!player.addItem(item)){
            level.addFreshEntity(new ItemEntity(level, interactionPosition.getX()+0.5, interactionPosition.getY()+0.5, interactionPosition.getZ()+0.5, item));
        }
    }
}
