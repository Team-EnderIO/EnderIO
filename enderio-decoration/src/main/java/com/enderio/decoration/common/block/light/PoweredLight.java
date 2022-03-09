package com.enderio.decoration.common.block.light;

import javax.annotation.Nullable;

import com.enderio.decoration.common.blockentity.PoweredLightBlockEntity;
import com.enderio.decoration.common.init.DecorBlockEntities;
import com.enderio.decoration.common.init.DecorBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredLight extends Light implements EntityBlock{
	public boolean wireless;

	public PoweredLight(Properties p_49224_, boolean inverted, boolean wireless) {
		super(p_49224_, inverted);
		this.wireless = wireless;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return DecorBlockEntities.POWERED_LIGHT.create(pos, state);
	}
	
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return level.isClientSide ? null : createTickerHelper(blockEntityType, DecorBlockEntities.POWERED_LIGHT.get(), PoweredLightBlockEntity::tick);
	}
	
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> p_152133_, BlockEntityType<E> p_152134_, BlockEntityTicker<? super E> p_152135_) {
		return p_152134_ == p_152133_ ? (BlockEntityTicker<A>)p_152135_ : null;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		if (!level.getBlockState(fromPos).is(DecorBlocks.LIGHT_NODE.get())) {
			if(level.getBlockEntity(pos) instanceof PoweredLightBlockEntity light) {
				light.needsUpdate();
			}
		}
	}
}
