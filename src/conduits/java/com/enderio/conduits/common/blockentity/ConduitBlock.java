package com.enderio.conduits.common.blockentity;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ConduitBlock extends Block {

    public ConduitBlock(Properties properties) {
        super(properties);
    }

    /**
     * Don't make Conduits tick for syncing reasons. Schedule a tick without delay, if the data has changed for conduitdata
     * @param state
     * @param level
     * @param pos
     * @param random
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        Optional.ofNullable(level.getBlockEntity(pos)).ifPresent(be -> {
            if (be instanceof EnderBlockEntity enderBlockEntity) {
                enderBlockEntity.sync();
            }
        });
    }
}
