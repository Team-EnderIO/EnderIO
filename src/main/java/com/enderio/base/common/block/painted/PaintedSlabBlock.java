package com.enderio.base.common.block.painted;

import com.enderio.base.common.blockentity.DoublePaintedBlockEntity;
import com.enderio.base.common.init.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;

import org.jetbrains.annotations.Nullable;

public class PaintedSlabBlock extends SlabBlock implements EntityBlock, IPaintedBlock {

    public PaintedSlabBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return DecorBlockEntities.DOUBLE_PAINTED.create(pos, state);
    }

    @Override
    public Block getPaint(BlockGetter level, BlockPos pos) {
        if (level.getBlockState(pos).getValue(SlabBlock.TYPE) != SlabType.BOTTOM
            && level.getExistingBlockEntity(pos) instanceof DoublePaintedBlockEntity paintedBlockEntity) {
            Block paint = paintedBlockEntity.getPaint2();
            if (paint != null)
                return paint;
        }
        return IPaintedBlock.super.getPaint(level, pos);
    }
}
