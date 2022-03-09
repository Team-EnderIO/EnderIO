package com.enderio.decoration.common.block.light;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class Light extends FaceAttachedHorizontalDirectionalBlock{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	protected static final VoxelShape CEILING_AABB_X = Block.box(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
	protected static final VoxelShape CEILING_AABB_Z = Block.box(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
	protected static final VoxelShape FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
	protected static final VoxelShape FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
	protected static final VoxelShape SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
	protected static final VoxelShape WEST_AABB = Block.box(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
	public boolean inverted;
	
	public Light(Properties p_53182_, boolean inverted) {
		super(p_53182_);
		this.inverted = inverted;
	}
	
	public void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, POWERED, FACE);
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		Direction direction = state.getValue(FACING);
		switch((AttachFace)state.getValue(FACE)) {
		case FLOOR:
			if (direction.getAxis() == Direction.Axis.X) {
				return FLOOR_AABB_X;
			}
			return FLOOR_AABB_Z;
		case WALL:
			switch(direction) {
			case EAST:
				return EAST_AABB;
			case WEST:
				return WEST_AABB;
			case SOUTH:
				return SOUTH_AABB;
			case NORTH:
			default:
				return NORTH_AABB;
			}
		case CEILING:
		default:
			if (direction.getAxis() == Direction.Axis.X) {
				return CEILING_AABB_X;
			} else {
				return CEILING_AABB_Z;
			}
		}
	}

}
