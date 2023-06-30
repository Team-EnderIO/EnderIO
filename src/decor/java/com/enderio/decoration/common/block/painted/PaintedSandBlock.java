package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.blockentity.SinglePaintedBlockEntity;
import com.enderio.decoration.common.entity.PaintedSandEntity;
import com.enderio.decoration.common.init.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class PaintedSandBlock extends SandBlock implements EntityBlock, IPaintedBlock {

    public PaintedSandBlock(Properties properties) {
        super(0, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pos, state);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRand) {
        if (isFree(pLevel.getBlockState(pPos.below())) && pPos.getY() >= pLevel.getMinBuildHeight()) {
            PaintedSandEntity paintedSandEntity = new PaintedSandEntity(pLevel, pPos.getX() + 0.5D, pPos.getY(), pPos.getZ() + 0.5D,
                pLevel.getBlockState(pPos));
            this.falling(paintedSandEntity);
            BlockEntity be = pLevel.getBlockEntity(pPos);
            if (be != null)
                paintedSandEntity.blockData = be.saveWithoutMetadata();
            pLevel.addFreshEntity(paintedSandEntity);
        }
    }

    @Override
    public int getDustColor(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        BlockEntity blockEntity = pReader.getBlockEntity(pPos);
        if (blockEntity instanceof SinglePaintedBlockEntity paintedBlockEntity) {
            Block block = paintedBlockEntity.getPaint();
            if (block != null) {
                return block.defaultMapColor().col;
            }
        }
        return 0;
    }
}
