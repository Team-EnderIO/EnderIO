package com.enderio.base.common.paint.block;

import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.paint.PaintedSandEntity;
import com.enderio.base.common.paint.blockentity.PaintedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ColoredFallingBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PaintedSandBlock extends ColoredFallingBlock implements EntityBlock, PaintedBlock {

    public PaintedSandBlock(Properties properties) {
        super(new ColorRGBA(0), properties);
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
            if (be != null) {
                paintedSandEntity.blockData = be.saveWithoutMetadata(pLevel.registryAccess());
            }

            pLevel.setBlock(pPos, pLevel.getBlockState(pPos).getFluidState().createLegacyBlock(), 3);
            pLevel.addFreshEntity(paintedSandEntity);
        }
    }

    @Override
    public int getDustColor(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        BlockEntity blockEntity = pReader.getBlockEntity(pPos);
        if (blockEntity instanceof PaintedBlockEntity paintedBlockEntity) {
            Optional<Block> block = paintedBlockEntity.getPrimaryPaint();
            if (block.isPresent()) {
                return block.get().defaultMapColor().col;
            }
        }
        return 0;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return getPaintedStack(level, pos, this);
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState,
        @Nullable BlockPos queryPos) {
        if (level.getBlockEntity(pos) instanceof PaintedBlockEntity painted) {
            Optional<Block> block = painted.getPrimaryPaint();

            if (block.isPresent()) {
                return block.get().defaultBlockState();
            }
        }
        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }
}
