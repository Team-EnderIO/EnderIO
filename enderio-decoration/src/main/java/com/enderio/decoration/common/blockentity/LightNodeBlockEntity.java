package com.enderio.decoration.common.blockentity;

import com.enderio.decoration.common.block.light.LightNode;
import com.enderio.decoration.common.network.EnderDecorNetwork;
import com.enderio.decoration.common.network.ServerToClientLightUpdate;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

public class LightNodeBlockEntity extends BlockEntity {
	public BlockPos masterpos;

	public LightNodeBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
		super(type, worldPosition, blockState);
	}
	
	public void setMaster(PoweredLightBlockEntity master) {
		this.masterpos = master.getBlockPos();
		this.setChanged();
	}
	
	/**
	 * called in {@link LightNode.neighborChanged} when a neighbor changes serverside.
	 * Checks if the this block still has a {@code PoweredLight}, if not it removes itself.
	 * Checks if the {@code PoweredLight} is still active, if not it removes itself.
	 * If the block changed inside the range, call the {@code PoweredLightBlockEntity} to update. //TODO make smarter? 
	 */
	public static void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving, LightNodeBlockEntity e) {
		if (e.masterpos == null) {
			return;
		}
		if (!(level.getBlockEntity(e.masterpos) instanceof PoweredLightBlockEntity)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_NEIGHBORS);
            EnderDecorNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new ServerToClientLightUpdate(pos, Blocks.AIR.defaultBlockState())); //custom setblock packet to update light
			return;
		}
		if (PoweredLightBlockEntity.inSpreadZone(fromPos, e.masterpos)) {
			PoweredLightBlockEntity master = (PoweredLightBlockEntity) level.getBlockEntity(e.masterpos);
			if (!master.isActive()) {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_NEIGHBORS);
				EnderDecorNetwork.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), new ServerToClientLightUpdate(pos, Blocks.AIR.defaultBlockState())); //custom setblock packet to update light
				return;
			}
			if (level.getBlockEntity(fromPos) instanceof LightNodeBlockEntity light) {
				if (light.masterpos != null && light.masterpos != e.masterpos) {
					master.needsUpdate();
					return;
				} 
			} else {
				master.needsUpdate();
				return;
			}
		}
	}
	
	@Override
	protected void saveAdditional(CompoundTag tag) {
		tag.put("masterpos", NbtUtils.writeBlockPos(this.masterpos));
		super.saveAdditional(tag);
	}
	
	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.masterpos = NbtUtils.readBlockPos(tag.getCompound("masterpos"));
	}
	
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}
}
