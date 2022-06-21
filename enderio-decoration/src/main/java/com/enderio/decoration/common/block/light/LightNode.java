package com.enderio.decoration.common.block.light;

import com.enderio.decoration.common.blockentity.LightNodeBlockEntity;
import com.enderio.decoration.common.init.DecorBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Dummy light block
 */
public class LightNode extends Block implements EntityBlock{

	public LightNode(Properties properties) {
		super(properties);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return DecorBlockEntities.LIGHT_NODE.create(pos, state);
	}
	
	@Override
	public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
		return true;
	}
	
	@Override
	public boolean canBeReplaced(BlockState state, Fluid fluid) {
		return true;
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Shapes.empty();
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (level.getBlockEntity(pos) instanceof LightNodeBlockEntity e) {
			LightNodeBlockEntity.neighborChanged(state, level, pos, block, fromPos, isMoving, e);
		}
	}

}
