package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.init.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PoweredBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public class PaintedRedstoneBlock extends PoweredBlock implements EntityBlock, IPaintedBlock {

    public PaintedRedstoneBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pos, state);
    }
}
