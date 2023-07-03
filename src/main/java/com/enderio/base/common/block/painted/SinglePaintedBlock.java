package com.enderio.base.common.block.painted;

import com.enderio.base.common.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SinglePaintedBlock extends Block implements EntityBlock, IPaintedBlock {
    public SinglePaintedBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return EIOBlockEntities.SINGLE_PAINTED.create(pPos, pState);
    }
}
