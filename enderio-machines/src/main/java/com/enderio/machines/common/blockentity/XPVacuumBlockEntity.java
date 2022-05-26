package com.enderio.machines.common.blockentity;

import com.enderio.base.common.blockentity.sync.IntegerDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.VacuumMachineEntity;
import com.enderio.machines.common.blockentity.data.sidecontrol.fluid.FluidTankMaster;
import com.enderio.machines.common.menu.XPVacuumMenu;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class XPVacuumBlockEntity extends VacuumMachineEntity<ExperienceOrb> {
    private FluidTankMaster fluidTank;
    
    public XPVacuumBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState, ExperienceOrb.class);
        this.fluidTank =  new FluidTankMaster(Integer.MAX_VALUE, getIoConfig()); //that seems quite large?
        addDataSlot(new IntegerDataSlot(() -> fluidTank.getFluidInTank(0).getAmount(), (i) -> fluidTank.setFluid(new FluidStack(Fluids.WATER, i)), SyncMode.WORLD));
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
	public void handleEntity(ExperienceOrb xpe) {
		int filled = fluidTank.fill(new FluidStack(Fluids.WATER, xpe.getValue()), FluidAction.EXECUTE);//TODO xp fluid
        if (filled == xpe.value) {
            xpe.discard();
        } else {
            xpe.value -= filled;
        }
	}

	@Override
	public MachineTier getTier() {
		return MachineTier.Standard;
	}
}
