package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.init.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SinglePaintedBlock extends BaseEntityBlock {
    public SinglePaintedBlock(Properties properties) {
        super(properties);
    }

    @Nonnull
    @Override
    public RenderShape getRenderShape(@Nonnull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pPos, @Nonnull BlockState pState) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pPos, pState);
    }
}
