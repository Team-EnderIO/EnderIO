package com.enderio.enderio.content.cold_fire;

import com.enderio.enderio.init.EIOBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, getFireTickDelay(level.getRandom()));
        if (!state.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
        }

        int age = state.getValue(AGE);
        int newAge = Math.min(15, age + random.nextInt(3) / 2);
        if (age != newAge) {
            state = state.setValue(AGE, newAge);
            level.setBlock(pos, state, 260);
        }

        if (!this.isValidFireLocation(level, pos)) {
            BlockPos below = pos.below();
            if (!level.getBlockState(below).isFaceSturdy(level, below, Direction.UP) || age > 3) {
                level.removeBlock(pos, false);
            }
            return;
        }

        if (age == 15 && random.nextInt(4) == 0 && !this.canCatchFire(level, pos.below(), Direction.UP)) {
            level.removeBlock(pos, false);
        }
    }

    private static int getFireTickDelay(RandomSource random) {
        return 30 + random.nextInt(10);
    }

    private boolean isValidFireLocation(BlockGetter level, BlockPos pos) {
        for(Direction direction : Direction.values()) {
            if (this.canCatchFire(level, pos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }

        return false;
    }

    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        return this.canSurvive(state, level, pos) ? this.getStateWithAge(level, pos, state.getValue(AGE)) : Blocks.AIR.defaultBlockState();
    }

    private BlockState getStateWithAge(LevelReader level, BlockPos pos, int age) {
        BlockState stateForPlacement = coldFireStateFromFireState(getState(level, pos));
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
        level.scheduleTick(pos, this, getFireTickDelay(level.getRandom()));
    }
}
