package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class ColdFireBlock extends FireBlock {
    public ColdFireBlock(Properties props) {
        super(props);
    }

    @Override
    public void tick(BlockState p_53449_, ServerLevel p_53450_, BlockPos p_53451_, Random p_53452_) {
        //don't spread
    }

    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateWithAge(LevelAccessor p_53438_, BlockPos p_53439_, int p_53440_) {
        return coldFireStateFromFireState(super.getStateWithAge(p_53438_, p_53439_, p_53440_));
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_49244_) {
        return coldFireStateFromFireState(getState(p_49244_.getLevel(), p_49244_.getClickedPos()));
    }

    private BlockState coldFireStateFromFireState(BlockState fireBlockState) {
        AtomicReference<BlockState> coldFireBlockState = new AtomicReference<>(defaultBlockState());
        fireBlockState.getProperties().stream().filter(prop -> coldFireBlockState.get().hasProperty(prop)).forEach(prop -> coldFireBlockState.set(coldFireBlockState.get().setValue((Property)prop, fireBlockState.getValue(prop))));
        return coldFireBlockState.get();
    }

    @Override
    public void entityInside(BlockState p_49260_, Level p_49261_, BlockPos p_49262_, Entity p_49263_) {
        //Don't deal fire damage
    }

    @Override
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState prevState, boolean movedByPiston) {
        //Just remove if it's invalid, don't try to open portals
        if (!newState.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
        }
    }
}
