package com.enderio.decoration.common.block.light;

import javax.annotation.Nullable;

import com.enderio.decoration.common.blockentity.PoweredLightBlockEntity;
import com.enderio.decoration.common.init.DecorBlockEntities;
import com.enderio.decoration.common.init.DecorBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;

/**
 * Class for all power consuming lights. 
 * Handles {@code PoweredLightBlockEntity} interactions.
 * Handles "Wireless" subtype.
 */
public class PoweredLight extends Light implements EntityBlock{
	public boolean wireless;

	public PoweredLight(Properties properties, boolean inverted, boolean wireless) {
		super(properties, inverted);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ENABLED, false).setValue(FACE, AttachFace.WALL));
		this.wireless = wireless;
	}

	public boolean isWireless() {
		return wireless;
	}
	
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return DecorBlockEntities.POWERED_LIGHT.create(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return createTickerHelper(blockEntityType, DecorBlockEntities.POWERED_LIGHT.get(), PoweredLightBlockEntity::tick);
	}
	
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeE, BlockEntityTicker<? super E> ticker) {
		return typeA == typeE ? (BlockEntityTicker<A>)ticker : null;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!level.getBlockState(fromPos).is(DecorBlocks.LIGHT_NODE.get())) {
			if(level.getBlockEntity(pos) instanceof PoweredLightBlockEntity light) {
				light.needsUpdate();
			}
		}
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
	}
	
	@Override
	public void checkPoweredState(Level level, BlockPos pos, BlockState state) {
		if (level.getBlockEntity(pos) instanceof PoweredLightBlockEntity light) {
			if (light.isActive()) {
				super.checkPoweredState(level, pos, state);
				return;
			}
		}
		level.setBlock(pos, state.setValue(ENABLED, false), 3);
	}
}
