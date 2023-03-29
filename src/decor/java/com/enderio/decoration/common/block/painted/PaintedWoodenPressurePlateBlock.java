package com.enderio.decoration.common.block.painted;

import com.enderio.decoration.common.init.DecorBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.Nullable;

public class PaintedWoodenPressurePlateBlock extends PressurePlateBlock implements EntityBlock, IPaintedBlock {

    public PaintedWoodenPressurePlateBlock(Properties properties) {
        super(Sensitivity.EVERYTHING, properties, BlockSetType.OAK); // TODO: 1.19.4 Sensible?
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return DecorBlockEntities.SINGLE_PAINTED.create(pos, state);
    }
}
