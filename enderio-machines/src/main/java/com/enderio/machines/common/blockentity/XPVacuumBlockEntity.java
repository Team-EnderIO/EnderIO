package com.enderio.machines.common.blockentity;

import java.util.ArrayList;
import java.util.List;

import com.enderio.base.common.blockentity.sync.IntegerDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.base.common.util.AttractionUtil;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class XPVacuumBlockEntity extends MachineBlockEntity {
    private static final double COLLISION_DISTANCE_SQ = 1 * 1;
    private static final double SPEED = 0.025;
    private static final double SPEED_4 = SPEED*4 ;
    private static final int MAX_RANGE = 6;
    private int range = 6;
    private FluidTankMaster fluidTank;
    private List<ExperienceOrb> xpEntities = new ArrayList<>();
    
    public XPVacuumBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineTier.STANDARD, pType, pWorldPosition, pBlockState);
        this.fluidTank =  new FluidTankMaster(Integer.MAX_VALUE, getIoConfig()); //that seems quite large?
        addDataSlot(new IntegerDataSlot(() -> fluidTank.getFluidInTank(0).getAmount(), (i) -> fluidTank.setFluid(new FluidStack(Fluids.WATER, i)), SyncMode.WORLD));
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
        if (this.getRedstoneControl().isActive(level.hasNeighborSignal(worldPosition))) {
            this.collectXP(this.getLevel(), this.getBlockPos(), this.range);
        }
        super.tick();
    }
    
    private void collectXP(Level level, BlockPos pos, int range) {
        if ((level.getGameTime() + pos.asLong()) % 5 == 0) {
            AABB area = new AABB(pos).inflate(range);
            this.xpEntities = level.getEntitiesOfClass(ExperienceOrb.class, area);
        }
        for (ExperienceOrb xpe: xpEntities) {
            if (AttractionUtil.moveToPos(xpe, pos, SPEED, SPEED_4, COLLISION_DISTANCE_SQ)) {
                int filled = fluidTank.fill(new FluidStack(Fluids.WATER, xpe.getValue()), FluidAction.EXECUTE);//TODO xp fluid
                if (filled == xpe.value) {
                    xpe.discard();
                    return;
                } else {
                    xpe.value -= filled;
                }
            }
        }
    }
    
    public int getRange() {
        return range;
    }
    
    public void setRange(int range) {
        this.range = range;
    }
    
    public void decreaseRange() {
        if (this.range > 0) {
            this.range--;
        }
    }
    
    public void increaseRange() {
        if (this.range < MAX_RANGE) {
            this.range++;
        }
    }
    
    @Override
    public void onLoad() {
        if (this.xpEntities.isEmpty()) {
            AABB area = new AABB(this.getBlockPos()).inflate(range);
            this.xpEntities = level.getEntitiesOfClass(ExperienceOrb.class, area);
        }
        super.onLoad();
    }
}
