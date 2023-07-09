package com.enderio.machines.common.blockentity;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.machines.common.blockentity.base.VacuumMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.menu.XPVacuumMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import static com.enderio.base.common.util.ExperienceUtil.EXP_TO_FLUID;

public class XPVacuumBlockEntity extends VacuumMachineBlockEntity<ExperienceOrb> {
    public XPVacuumBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState, ExperienceOrb.class);

        // Sync fluid level.
        addDataSlot(new IntegerNetworkDataSlot(
            () -> getFluidTankNN().getFluidInTank(0).getAmount(),
            i -> getFluidTankNN().setFluid(new FluidStack(EIOFluids.XP_JUICE.getSource(), i))
        ));
    }

    @Override
    public String getColor() {
        return MachinesConfig.CLIENT.BLOCKS.XP_VACUUM_RANGE_COLOR.get();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new XPVacuumMenu(this, inventory, containerId);
    }

    @Override
    public void handleEntity(ExperienceOrb xpe) {
        int filled = getFluidTankNN().fill(new FluidStack(EIOFluids.XP_JUICE.getSource(), xpe.getValue() * EXP_TO_FLUID), FluidAction.EXECUTE);
        if (filled == xpe.getValue() * EXP_TO_FLUID) {
            xpe.discard();
        } else {
            xpe.value -= filled / ((float) EXP_TO_FLUID);
        }
    }

    // region Fluid Storage

    @Override
    protected @Nullable FluidTank createFluidTank() {
        return new FluidTank(Integer.MAX_VALUE) {
            @Override
            protected void onContentsChanged() {
                super.onContentsChanged();
                setChanged();
            }
        };
    }

    // endregion
}