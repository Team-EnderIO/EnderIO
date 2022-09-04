package com.enderio.base.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.concurrent.atomic.AtomicReference;

public class ColdFireBlock extends FireBlock {
    public ColdFireBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        //don't spread
    }

    @SuppressWarnings("deprecation")
    @Override
    protected boolean canBurn(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateWithAge(LevelAccessor level, BlockPos pos, int age) {
        return coldFireStateFromFireState(super.getStateWithAge(level, pos, age));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return coldFireStateFromFireState(getState(ctx.getLevel(), ctx.getClickedPos()));
    }

    private BlockState coldFireStateFromFireState(BlockState fireBlockState) {
        AtomicReference<BlockState> coldFireBlockState = new AtomicReference<>(defaultBlockState());
        fireBlockState.getProperties().stream().filter(prop -> coldFireBlockState.get().hasProperty(prop)).forEach(prop -> coldFireBlockState.set(coldFireBlockState.get().setValue((Property)prop, fireBlockState.getValue(prop))));
        return coldFireBlockState.get();
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
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
