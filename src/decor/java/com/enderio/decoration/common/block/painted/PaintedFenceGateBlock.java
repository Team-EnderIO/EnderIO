package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.init.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.Nullable;

public class PaintedFenceGateBlock extends FenceGateBlock implements EntityBlock, IPaintedBlock {

    public PaintedFenceGateBlock(Properties properties) {
        super(properties, WoodType.OAK); // TODO: 1.19.4: Sensible value?
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pos, state);
    }

}
