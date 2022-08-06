package com.enderio.conduits.common.blocks;

import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConduitBlock extends Block implements EntityBlock {

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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ConduitBlockEntities.CONDUIT.create(pos, state);
    }
}
