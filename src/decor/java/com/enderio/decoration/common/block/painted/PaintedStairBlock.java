package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.blockentity.SinglePaintedBlockEntity;
import com.enderio.decoration.common.init.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PaintedStairBlock extends StairBlock implements EntityBlock, IPaintedBlock {

    public PaintedStairBlock(Properties properties) {
        super(Blocks.OAK_PLANKS::defaultBlockState, properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pos, state);
    }
}
