package com.enderio.machines.common.blockentity;

import com.enderio.base.common.config.BaseConfig;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.VacuumMachineEntity;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.menu.XPVacuumMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XPVacuumBlockEntity extends VacuumMachineEntity<ExperienceOrb> {
    private final FluidTank fluidTank;

    private final MachineFluidHandler fluidHandler;

    private final LazyOptional<MachineFluidHandler> fluidHandlerCap;

    public XPVacuumBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState, ExperienceOrb.class);

        String color = BaseConfig.COMMON.BLOCKS.XP_VACUUM_RANGE_COLOR.get();
        this.bCol = (float)Integer.parseInt(color.substring(0,2), 16) / 255;
        this.gCol = (float)Integer.parseInt(color.substring(2,4), 16) / 255;
        this.rCol = (float)Integer.parseInt(color.substring(4,6), 16) / 255;

        // Create fluid tank.
        this.fluidTank = createFluidTank(Integer.MAX_VALUE);

        // Create fluid tank storage.
        this.fluidHandler = new MachineFluidHandler(getIOConfig(), fluidTank);
        this.fluidHandlerCap = LazyOptional.of(() -> fluidHandler);

        // Add capability provider
        addCapabilityProvider(fluidHandler);
        addDataSlot(new IntegerDataSlot(() -> fluidTank.getFluidInTank(0).getAmount(), (i) -> fluidTank.setFluid(new FluidStack(EIOFluids.XP_JUICE.get(), i)),
            SyncMode.WORLD));
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("tank", fluidTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        fluidTank.readFromNBT(pTag.getCompound("tank"));
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new XPVacuumMenu(this, inventory, containerId);
    }

    @Override
    public void handleEntity(ExperienceOrb xpe) {
        int filled = fluidTank.fill(new FluidStack(EIOFluids.XP_JUICE.get(), xpe.getValue() * 20), FluidAction.EXECUTE);
        if (filled == xpe.getValue() * 20) {
            xpe.discard();
        } else {
            xpe.value -= filled / 20f;
        }
    }


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && side == null) {
            return fluidHandlerCap.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandlerCap.invalidate();
    }

    private FluidTank createFluidTank(int capacity) {
        return new FluidTank(capacity) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }
        };
    }
}