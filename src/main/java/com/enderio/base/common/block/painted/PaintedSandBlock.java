package com.enderio.base.common.block.painted;

import com.enderio.base.common.blockentity.SinglePaintedBlockEntity;
import com.enderio.base.common.entity.PaintedSandEntity;
import com.enderio.base.common.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class PaintedSandBlock extends SandBlock implements EntityBlock, IPaintedBlock {

    public PaintedSandBlock(Properties properties) {
        super(0, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return EIOBlockEntities.SINGLE_PAINTED.create(pos, state);
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
            pLevel.setBlock(pPos, pLevel.getBlockState(pPos).getFluidState().createLegacyBlock(), 3);
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

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return getPaintedStack(level, pos, this);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState,
        @Nullable BlockPos queryPos) {
        if (level.getBlockEntity(pos) instanceof SinglePaintedBlockEntity painted && painted.getPaint() != null) {
            return painted.getPaint().defaultBlockState();
        }
        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }
}
