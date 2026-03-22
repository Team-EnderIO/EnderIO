package com.enderio.enderio.content.cold_fire;

import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Blocks;
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

    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return this.canSurvive(state, level, pos) ? this.getStateWithAge(level, pos, state.getValue(AGE)) : Blocks.AIR.defaultBlockState();
    }

    private BlockState getStateWithAge(LevelReader level, BlockPos pos, int age) {
        BlockState stateForPlacement = getState(level, pos);
        return stateForPlacement.is(EIOBlocks.COLD_FIRE) ? stateForPlacement.setValue(AGE, age) : stateForPlacement;
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
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier insideBlockEffectApplier,
        boolean intersects) {
        // No op: Don't deal fire damage
    }

    @Override
    public void onPlace(BlockState newState, Level level, BlockPos pos, BlockState prevState, boolean movedByPiston) {
        //Just remove if it's invalid, don't try to open portals
        if (!newState.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
        }
    }
}
