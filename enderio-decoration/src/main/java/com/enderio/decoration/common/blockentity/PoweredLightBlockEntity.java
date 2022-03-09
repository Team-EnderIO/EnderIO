package com.enderio.decoration.common.blockentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.enderio.decoration.common.init.DecorBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class PoweredLightBlockEntity extends BlockEntity{
	static int spread = 2;
	private boolean update = true;

	public PoweredLightBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
		super(type, worldPosition, blockState);
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, PoweredLightBlockEntity e) {
		if (e.update) {
			createNodes(level, pos, state);
			e.update = false;
		}
	}
	
	public void needsUpdate() {
		this.update = true;
	}

	private static void createNodes(Level level, BlockPos center, BlockState state) {
		ArrayList<BlockPos> start = new ArrayList<BlockPos>();
		ArrayList<BlockPos> prev = new ArrayList<BlockPos>();
		ArrayList<BlockPos> blocked = new ArrayList<BlockPos>();
		HashSet<BlockPos> next;
		start.add(center);
		while (!start.isEmpty()) {
			next = new HashSet<BlockPos>();
			for (int i=0; i < start.size(); i++) {
				next.addAll(spreadNode(level, start.get(i), center, blocked, prev));
			}
			prev = new ArrayList<>();
			prev.addAll(start);
			start = new ArrayList<BlockPos>();
			start.addAll(next);
		}
	}
	
	/**
	 * Add Light nodes to the axis of the center block. If the position is already occupied, it is added to the blocked list. 
	 * If the position is not blocked by a block in the blocked list and is within the spread zone, it is added to the array of starting positions for the next iteration.
	 * @param level
	 * @param node
	 * @param center
	 * @param blocked
	 * @param prev 
	 * @return The next starting positions for the algorithm.
	 */
	private static List<BlockPos> spreadNode(Level level, BlockPos node, BlockPos center, List<BlockPos> blocked, ArrayList<BlockPos> prev) {
		ArrayList<BlockPos> next = new ArrayList<>();
		for (Direction dir : Direction.values()) {
			BlockPos relative = node.relative(dir);
			if (!prev.contains(relative) && inSpreadZone(relative, center)) {
				next.add(relative);
				if (level.getBlockState(relative).isAir()) {
					level.setBlockAndUpdate(relative, DecorBlocks.LIGHT_NODE.get().defaultBlockState());
					if (level.getBlockEntity(relative) instanceof LightNodeBlockEntity light) {
						light.setMaster((PoweredLightBlockEntity) level.getBlockEntity(center));
					}
				}
				boolean bl = false;
				for (BlockPos blockedpos: blocked) {
					if (isBlocked(relative, center, blockedpos) && level.getBlockState(relative).is(DecorBlocks.LIGHT_NODE.get())) {
						level.setBlock(relative, Blocks.AIR.defaultBlockState(), 3);
						bl = true;
					}
				}
				if (!level.getBlockState(relative).isAir() && !level.getBlockState(relative).is(DecorBlocks.LIGHT_NODE.get()) && !bl) {
					blocked.add(relative);
				}
			}
		}
		return next;
	}
	
	/**
	 * Check if the light is blocked by another block. For now this means any that the block is inside a cube 
	 * created away from the center, with the blocked position as edge.
	 * @param node
	 * @param center
	 * @param blocked
	 * @return
	 */
	private static boolean isBlocked(BlockPos node, BlockPos center, BlockPos blocked) {
		boolean x = false;
		boolean y = false;
		boolean z = false;
		if (center.getX() >= blocked.getX() && blocked.getX() >= node.getX()) {
			x = true;
		}
		if (center.getX() <= blocked.getX() && blocked.getX() <= node.getX()) {
			x = true;
		}
		if (center.getY() >= blocked.getY() && blocked.getY() >= node.getY()) {
			y = true;
		}
		if (center.getY() <= blocked.getY() && blocked.getY() <= node.getY()) {
			y = true;
		}
		if (center.getZ() >= blocked.getZ() && blocked.getZ() >= node.getZ()) {
			z = true;
		}
		if (center.getZ() <= blocked.getZ() && blocked.getZ() <= node.getZ()) {
			z = true;
		}
		return x && y && z;
	}
	
	/**
	 * Returns true if the block is inside the zone defined by the spread.
	 * @param node
	 * @param center
	 * @return
	 */
	public static boolean inSpreadZone(BlockPos node, BlockPos center) { 
		if (Math.abs(node.getX()-center.getX()) > spread) {
			return false;
		}
		if (Math.abs(node.getY()-center.getY()) > spread) {
			return false;
		}
		if (Math.abs(node.getZ()-center.getZ()) > spread) {
			return false;
		}
		return true;
	}
	

}
