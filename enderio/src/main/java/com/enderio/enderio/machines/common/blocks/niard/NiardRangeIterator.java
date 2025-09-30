package com.enderio.enderio.machines.common.blocks.niard;

import com.enderio.enderio.machines.common.attachment.ActionRange;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapted from older Ender IO versions
 */
public class NiardRangeIterator {
    private final List<BlockPos> positions = new ArrayList<>();
    private int index = 0;

    public NiardRangeIterator(BlockPos bc, ActionRange actionRange) {
        int radius = actionRange.range();
        positions.add(bc);

        for (int i = 1; i <= radius; i++) {
            for (int j = -i; j < i; j++) {
                positions.add(new BlockPos(bc.getX() - i, bc.getY() - 1, bc.getZ() + j));
                positions.add(new BlockPos(bc.getX() + i, bc.getY() - 1, bc.getZ() - j));
                positions.add(new BlockPos(bc.getX() + j, bc.getY()- 1, bc.getZ() + i));
                positions.add(new BlockPos(bc.getX() - j, bc.getY()- 1, bc.getZ() - i));
            }
        }
    }

    public BlockPos current() {
        return positions.get(index);
    }

    public void moveToNextPosition() {
        index  = (index + 1) % size();
    }

    public int size() {
        return positions.size();
    }

    public int getIndex() {
        return index;
    }
}
