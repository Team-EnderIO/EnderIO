package com.enderio.core.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class BlockUtil {

    /**
     * Removes a block as if mined by the player. All events, drops etc are triggered but Item#mineBlock is not called on the provided tool.
     * @param level the level containing the block to be removed
     * @param player the player mining the block
     * @param tool the tool being used
     * @param pos the position of the block to be removed
     * @return true if the block was removed
     */
    public static boolean removeBlock(Level level, Player player, ItemStack tool, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        boolean removed = state.onDestroyedByPlayer(level, pos, player, true, level.getFluidState(pos));
        if (removed) {
            state.getBlock().destroy(level, pos, state);
            state.getBlock().playerDestroy(level, player, pos, state, null, tool);
            /*
             * if (level instanceof ServerLevel serverLevel) {
             * state.getBlock().popExperience(serverLevel, pos, event.getExpToDrop()); }
             */
        }
        return removed;
    }
}
