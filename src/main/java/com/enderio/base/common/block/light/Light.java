package com.enderio.base.common.block.light;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Default might class. 
 * Handles shape related code.
 * Holds "inverted" property
 */
public class Light extends FaceAttachedHorizontalDirectionalBlock {

    public static final MapCodec<Light> CODEC = RecordCodecBuilder.mapCodec(
        inst -> inst.group(
                Codec.BOOL.fieldOf("inverted").forGetter(i -> i.inverted),
                propertiesCodec()
            )
            .apply(inst, Light::new)
    );

	public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
	protected static final VoxelShape CEILING_AABB_X = Block.box(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
	protected static final VoxelShape CEILING_AABB_Z = Block.box(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
	protected static final VoxelShape FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
	protected static final VoxelShape FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
	protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
	protected static final VoxelShape WEST_AABB = Block.box(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
	protected final boolean inverted;
	
	public Light(boolean inverted, Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ENABLED, !inverted).setValue(FACE, AttachFace.WALL));
		this.inverted = inverted;
	}
	
	public boolean isInverted() {
		return inverted;
	}

    @Override
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ENABLED, FACE);
	}

    @Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);
		switch(state.getValue(FACE)) {
		case FLOOR:
			if (direction.getAxis() == Direction.Axis.X) {
				return FLOOR_AABB_X;
			}
			return FLOOR_AABB_Z;
		case WALL:
            return switch (direction) {
                case EAST -> EAST_AABB;
                case WEST -> WEST_AABB;
                case SOUTH -> SOUTH_AABB;
                default -> NORTH_AABB;
            };
		case CEILING:
		default:
			if (direction.getAxis() == Direction.Axis.X) {
				return CEILING_AABB_X;
			} else {
				return CEILING_AABB_Z;
			}
		}
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction direction) {
		return true;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		this.checkPoweredState(level, pos, state);
	}
	
	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
		if (!oldState.is(state.getBlock())) {
			this.checkPoweredState(level, pos, state);
		}
	}
	
	public void checkPoweredState(Level level, BlockPos pos, BlockState state) {
		boolean powered = level.hasNeighborSignal(pos);
		if (powered != this.inverted ? state.getValue(ENABLED) : !state.getValue(ENABLED)) {
			level.setBlock(pos, state.setValue(ENABLED, this.inverted ? powered : !powered), 3);
		}
		
	}

    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
