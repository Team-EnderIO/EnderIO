package com.enderio.decoration.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class LightNodeBlockEntity extends BlockEntity{
	public PoweredLightBlockEntity master;

	public LightNodeBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
		super(type, worldPosition, blockState);
	}
	
	public void setMaster(PoweredLightBlockEntity master) {
		this.master = master;
		this.setChanged();
	}
	
	public static void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving, LightNodeBlockEntity e) {
		if (e.master == null) {
			return;
		}
		if (!(level.getBlockEntity(e.master.getBlockPos()) instanceof PoweredLightBlockEntity)) {
			level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		}
		if (PoweredLightBlockEntity.inSpreadZone(fromPos, e.master.getBlockPos())) {
			if (level.getBlockEntity(fromPos) instanceof LightNodeBlockEntity light) {
				if (light.master != null && light.master != e.master) {
					e.master.needsUpdate();
					return;
				} 
			} else {
				e.master.needsUpdate();
				return;
			}
		}
	}
}
