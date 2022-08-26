package com.enderio.conduits.common.blocks;

import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.init.ConduitBlockEntities;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ConduitBlock extends Block implements EntityBlock, SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public ConduitBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    /**
     * Don't make Conduits tick for syncing reasons. Schedule a tick without delay, if the data has changed for conduitdata
     * @param state
     * @param level
     * @param pos
     * @param random
     */
    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        Optional.ofNullable(level.getBlockEntity(pos)).ifPresent(be -> {
            if (be instanceof EnderBlockEntity enderBlockEntity) {
                enderBlockEntity.sync();
            }
        });
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos,
        BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return ConduitBlockEntities.CONDUIT.create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            return conduit.getShape().getTotalShape();
        }
        return Shapes.block();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            conduit.getShape().getConduit(hit.getBlockPos(), hit); //TODO: Yeta use
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {

        return (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof ConduitBlockEntity conduitBlockEntity)
                conduitBlockEntity.everyTick();
        };
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        HitResult hit = player.pick(player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + 5,1,false);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ConduitBlockEntity conduit) {
            @Nullable
            IConduitType conduitType = conduit.getShape().getConduit(((BlockHitResult)hit).getBlockPos(), hit);
            if (conduitType == null || conduit.removeType(conduitType)) {
                return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
            }
        }
        return false;
    }
}
