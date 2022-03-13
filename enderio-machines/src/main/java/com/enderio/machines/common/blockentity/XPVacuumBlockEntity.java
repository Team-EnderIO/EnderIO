package com.enderio.machines.common.blockentity;

import java.util.List;

import com.enderio.base.common.blockentity.sync.FluidStackDataSlot;
import com.enderio.base.common.blockentity.sync.IntegerDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.fluid.FluidTankMaster;
import com.enderio.machines.common.menu.XPVacuumMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class XPVacuumBlockEntity extends MachineBlockEntity {
	private static final double COLLISION_DISTANCE_SQ = 1 * 1;
    private static final double SPEED = 0.025;
    private static final double SPEED_4 = SPEED ;
	private static final int MAX_RANGE = 6;
	private int range = 6;
	private FluidTankMaster fluidTank;

	public XPVacuumBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition,
			BlockState pBlockState) {
		super(MachineTier.STANDARD, pType, pWorldPosition, pBlockState);
		this.fluidTank =  new FluidTankMaster(Integer.MAX_VALUE, getIoConfig()); //that seems quite large?
		addDataSlot(new FluidStackDataSlot(() -> fluidTank.getFluidInTank(0), fluidTank::setFluid, SyncMode.WORLD));
		add2WayDataSlot(new IntegerDataSlot(() -> this.range, this::setRange, SyncMode.GUI));
	}
	
	public FluidTankMaster getFluidTank() {
		return fluidTank;
	}
	
	@Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        // TODO: Rename to fluid or tank.
        // TODO: Common place for all NBT names.
        pTag.put("Fluids", fluidTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        fluidTank.readFromNBT(pTag.getCompound("Fluids"));
    }

	@Override
	public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
		return new XPVacuumMenu(this, inventory, containerId);
	}
	
	@Override
	public void tick() {
		super.tick();
		if (this.getRedstoneControl().isActive(level.hasNeighborSignal(worldPosition))) {
			this.collectXP(this.getLevel(), this.getBlockPos(), this.range);
		}
	}
	
	private void collectXP(Level level, BlockPos pos, int range) {
		AABB area = new AABB(pos).inflate(range);
		List<ExperienceOrb> xpEntities = level.getEntitiesOfClass(ExperienceOrb.class, area);
		for (ExperienceOrb xpe: xpEntities) { //magnet code
			double x = pos.getX() + 0.5D - xpe.getX();
			double y = pos.getY() + 0.5D - xpe.getY();
			double z = pos.getZ() + 0.5D - xpe.getZ();
			
			double distanceSq = x * x + y * y + z * z;
			
			if (distanceSq < COLLISION_DISTANCE_SQ) {
				int filled = fluidTank.fill(new FluidStack(Fluids.WATER, xpe.getValue()), FluidAction.EXECUTE);//TODO xp fluid
				if (filled == xpe.value) {
					xpe.discard();
					return;
				} else {
					xpe.value -= filled;
				}
			} else {
				double adjustedSpeed = SPEED_4 / distanceSq;
				Vec3 mov = xpe.getDeltaMovement();
				double deltaX = mov.x + x * adjustedSpeed;
				double deltaZ = mov.z + z * adjustedSpeed;
				double deltaY;
				if (y > 0) {
					deltaY = 0.12;
				} else {
					deltaY = mov.y + y * SPEED;
				}
				xpe.setDeltaMovement(deltaX, deltaY, deltaZ);
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
		}
	}
	
	public void increasseRange() {
		if (this.range < MAX_RANGE) {
			this.range++;
		}
	}
}
