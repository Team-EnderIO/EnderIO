package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ResettingLeverBlock extends LeverBlock {

    private final int delay;
    private final boolean inverted;

    public ResettingLeverBlock(int delaySeconds, boolean inverted) {
        super(Properties.ofFullCopy(Blocks.LEVER));
        this.delay = delaySeconds * 20;
        this.inverted = inverted;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, BlockHitResult pHit) {
        if (!pState.getValue(POWERED)) {
            pLevel.scheduleTick(pPos, this, delay);
        }

        return super.useWithoutItem(pState, pLevel, pPos, pPlayer, pHit);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        super.tick(state, level, pos, randomSource);

        if (state.getValue(POWERED) && !level.isClientSide) {
            this.pull(state, level, pos, null);
        }
    }

    @Override
    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        int res = super.getSignal(pBlockState, pBlockAccess, pPos,pSide);
        return inverted ? 15 - res : res;
    }

    @Override
    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        int res = super.getDirectSignal(pBlockState, pBlockAccess, pPos, pSide);
        return inverted ? 15 - res : res;
    }

}
