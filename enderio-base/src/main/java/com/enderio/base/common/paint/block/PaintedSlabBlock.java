package com.enderio.base.common.paint.block;

import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.paint.BlockPaintData;
import com.enderio.base.common.paint.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.paint.blockentity.PaintedBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PaintedSlabBlock extends SlabBlock implements EntityBlock, PaintedBlock {

    public PaintedSlabBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return EIOBlockEntities.DOUBLE_PAINTED.create(pos, state);
    }

    @Override
    public Block getPaint(BlockGetter level, BlockPos pos) {
        if (level.getBlockState(pos).getValue(SlabBlock.TYPE) != SlabType.BOTTOM
            && level.getBlockEntity(pos) instanceof PaintedBlockEntity paintedBlockEntity) {
            Optional<Block> paint = paintedBlockEntity.getSecondaryPaint();
            if (paint.isPresent() && !(paint.get() instanceof PaintedBlock)) {
                return paint.get();
            }
        }

        return PaintedBlock.super.getPaint(level, pos);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this);
        if (level.getBlockEntity(pos) instanceof DoublePaintedBlockEntity paintedBlockEntity) {
            Optional<Block> paint;
            if (target.getLocation().y - pos.getY() > 0.5) {
                paint = paintedBlockEntity.getSecondaryPaint();
            } else {
                paint = paintedBlockEntity.getPrimaryPaint();
            }

            paint.ifPresent(block -> stack.set(EIODataComponents.BLOCK_PAINT, BlockPaintData.of(block)));
        }

        return stack;
    }

    @Override
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState,
        @Nullable BlockPos queryPos) {
        if (level.getBlockEntity(pos) instanceof DoublePaintedBlockEntity painted) {
            var paint1 = painted.getPrimaryPaint();
            var paint2 = painted.getSecondaryPaint();

            // TODO: Safety check for PaintedBlock.

            if (side == Direction.UP && paint2.isPresent()) {
                return paint2.get().defaultBlockState();
            }

            if (side == Direction.DOWN && paint1.isPresent()) {
                return paint1.get().defaultBlockState();
            }

            if (paint1.isPresent()) {
                return paint1.get().defaultBlockState();
            }

            if (paint2.isPresent()) {
                return paint2.get().defaultBlockState();
            }
        }

        return super.getAppearance(state, level, pos, side, queryState, queryPos);
    }
}
