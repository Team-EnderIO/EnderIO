package com.enderio.enderio.content.misc_blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ResettingLeverBlock extends LeverBlock {

    private final int delay;
    private final boolean inverted;

    public ResettingLeverBlock(Properties properties, int delaySeconds, boolean inverted) {
        super(properties);
        this.delay = delaySeconds * 20;
        this.inverted = inverted;
    }

    public int delay() {
        return delay;
    }

    public boolean inverted() {
        return inverted;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!state.getValue(POWERED)) {
            level.scheduleTick(pos, this, delay);
        }

        return super.useWithoutItem(state, level, pos, player, hit);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        super.tick(state, level, pos, randomSource);

        if (state.getValue(POWERED) && !level.isClientSide()) {
            this.pull(state, level, pos, null);
        }
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        int res = super.getSignal(blockState, blockAccess, pos,side);
        return inverted ? 15 - res : res;
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        int res = super.getDirectSignal(blockState, blockAccess, pos, side);
        return inverted ? 15 - res : res;
    }

}
