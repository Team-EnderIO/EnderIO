package com.enderio.machines.common.utility;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TreeHelper {

    public static boolean isTree(BlockState blockState) {
        return blockState.is(BlockTags.LOGS) || blockState.is(BlockTags.LEAVES);
    }

    public static Set<BlockPos> getTree(Level level, BlockPos bottom, Predicate<BlockPos> inRange) {
        LinkedList<BlockPos> candidates = new LinkedList<>();
        HashSet<BlockPos> tree = new HashSet<>();
        HashSet<BlockPos> seen = new HashSet<>();

        candidates.add(bottom);

        while (!candidates.isEmpty()) {
            BlockPos pos = candidates.removeFirst();
            BlockState state = level.getBlockState(pos);
            seen.add(pos);
            if (isTree(state) && inRange.test(pos)) {
                tree.add(pos);
                BlockPos.betweenClosed(pos.offset(1, 1, 1), pos.offset(-1, -1, -1)).forEach(next -> {
                    if (seen.contains(next))
                        return;
                    candidates.add(next);
                });
            }
        }
        return tree;
    }
}
