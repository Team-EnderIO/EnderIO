package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.init.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.Nullable;

public class PaintedTrapDoorBlock extends TrapDoorBlock implements EntityBlock, IPaintedBlock {

    public PaintedTrapDoorBlock(Properties properties) {
        super(properties, BlockSetType.OAK);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pos, state);
    }
}
