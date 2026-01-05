package com.enderio.enderio.content.machines.block_detector;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class BlockDetectorBlock extends DirectionalBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final MapCodec<BlockDetectorBlock> CODEC = simpleCodec(BlockDetectorBlock::new);

    public BlockDetectorBlock(Properties p_55926_) {
        super(p_55926_);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(FACING, Direction.SOUTH).setValue(POWERED, Boolean.valueOf(false)));
    }

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos,
            @Nullable Direction direction) {
        return state.getValue(FACING) == direction;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (!canConnectRedstone(state, level, pos, direction)) {
            state.setValue(POWERED, false);
            return 0;
        }
        if (level.getBlockState(pos.relative(state.getValue(FACING))).isAir()) {
            state.setValue(POWERED, false);
            return 0;
        }
        state.setValue(POWERED, true);
        return 15;
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return getSignal(state, level, pos, direction);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(FACING) == direction) {
            level.blockUpdated(pos, state.getBlock());
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
}
