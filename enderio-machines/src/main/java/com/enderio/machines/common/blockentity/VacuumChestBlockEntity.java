package com.enderio.machines.common.blockentity;

import java.util.List;
import java.util.Optional;

import com.enderio.base.common.blockentity.sync.IntegerDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemHandlerMaster;
import com.enderio.machines.common.blockentity.data.sidecontrol.item.ItemSlotLayout;
import com.enderio.machines.common.menu.VacuumChestMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class VacuumChestBlockEntity extends MachineBlockEntity {
	private static final double COLLISION_DISTANCE_SQ = 1 * 1;
    private static final double SPEED = 0.025;
    private static final double SPEED_4 = SPEED ;
	private static final int MAX_RANGE = 6;
	private int range = 6;

	public VacuumChestBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition,
			BlockState pBlockState) {
		super(MachineTier.STANDARD, pType, pWorldPosition, pBlockState);
		add2WayDataSlot(new IntegerDataSlot(() -> this.range, this::setRange, SyncMode.GUI));
	}

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new VacuumChestMenu(this, inventory, containerId);
	}
	
	@Override
	public Optional<ItemSlotLayout> getSlotLayout() {
		return Optional.of(ItemSlotLayout.basic(28,0));
	}
	
	@Override
	protected ItemHandlerMaster createItemHandler(ItemSlotLayout layout) {
		return new ItemHandlerMaster(getIoConfig(), layout) {
			@Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
			
			@Override
			public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
				if (slot == 27) {
					return stack;
				}
				return super.insertItem(slot, stack, simulate);
			}
		};
	}
	
	@Override
	public void tick() {
		super.tick();
		if (this.getRedstoneControl().isActive(level.hasNeighborSignal(worldPosition))) {
			this.collectItems(this.getLevel(), this.getBlockPos(), this.range);
		}
	}
	
	private void collectItems(Level level, BlockPos pos, int range) {
		AABB area = new AABB(pos).inflate(range);
		List<ItemEntity> itemEntities = level.getEntitiesOfClass(ItemEntity.class, area, e -> true); //TODO filter logic
		for (ItemEntity ie: itemEntities) { //magnet code
			double x = pos.getX() + 0.5D - ie.getX();
			double y = pos.getY() + 0.5D - ie.getY();
			double z = pos.getZ() + 0.5D - ie.getZ();
			
			double distanceSq = x * x + y * y + z * z;
			
			if (distanceSq < COLLISION_DISTANCE_SQ) {
				for (int i=0; i<this.getItemHandler().getSlots();i++) {
					ItemStack reminder = this.getItemHandler().insertItem(i, ie.getItem().copy(), false);
					if (reminder.isEmpty()) {
						ie.discard();
						return;
					} else {
						ie.getItem().setCount(reminder.getCount());
					}
				}
			} else {
				double adjustedSpeed = SPEED_4 / distanceSq;
				Vec3 mov = ie.getDeltaMovement();
				double deltaX = mov.x + x * adjustedSpeed;
				double deltaZ = mov.z + z * adjustedSpeed;
				double deltaY;
				if (y > 0) {
					deltaY = 0.12;
				} else {
					deltaY = mov.y + y * SPEED;
				}
				ie.setDeltaMovement(deltaX, deltaY, deltaZ);
			}
		}
	}
	
	public int getRange() {
		return range;
	}
	
	public void setRange(int range) {
		this.range = range;
	}
	
	public void decreasseRange() {
		if (this.range > 0) {
			this.range--;
			System.out.println(this.range);
		}
	}
	
	public void increasseRange() {
		if (this.range < MAX_RANGE) {
			this.range++;
			System.out.println(this.range);
		}
	}
}
