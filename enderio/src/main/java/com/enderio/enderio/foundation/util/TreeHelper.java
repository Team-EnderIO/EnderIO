package com.enderio.enderio.foundation.util;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class TreeHelper {

    public static boolean isTree(BlockState blockState) {
        return blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES);
    }

    public static Set<BlockPos> getTree(Level level, BlockPos bottom, Predicate<BlockPos> inRange) {
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();
        HashSet<BlockPos> tree = new HashSet<>();
        HashSet<BlockPos> seen = new HashSet<>();

        queue.add(bottom);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.removeFirst();
            BlockState state = level.getBlockState(pos);

            if(isTree(state) && inRange.test(pos)) {
                tree.add(pos);
                BlockPos.betweenClosedStream(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))
                    .forEach( next ->  {
                        if (seen.contains(next)) return;

                        var immutable = next.immutable();
                        seen.add(immutable);
                        queue.add(immutable);
                    });
            }
        }
        return tree;
    }
}
