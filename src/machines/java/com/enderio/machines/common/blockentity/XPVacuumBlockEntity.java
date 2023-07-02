package com.enderio.machines.common.blockentity;

import com.enderio.base.common.init.EIOFluids;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.VacuumMachineBlockEntity;
import com.enderio.machines.common.config.client.MachinesClientConfig;
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

import static com.enderio.base.common.util.ExperienceUtil.EXPTOFLUID;

public class XPVacuumBlockEntity extends VacuumMachineBlockEntity<ExperienceOrb> {
    public XPVacuumBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState, ExperienceOrb.class);

        String color = MachinesClientConfig.BLOCKS.XP_VACUUM_RANGE_COLOR.get();
        this.rCol = (float)Integer.parseInt(color.substring(0,2), 16) / 255;
        this.gCol = (float)Integer.parseInt(color.substring(2,4), 16) / 255;
        this.bCol = (float)Integer.parseInt(color.substring(4,6), 16) / 255;

        // Sync fluid level.
        addDataSlot(new IntegerDataSlot(() -> getFluidTankNN().getFluidInTank(0).getAmount(), (i) -> getFluidTankNN().setFluid(new FluidStack(EIOFluids.XP_JUICE.get(), i)),
            SyncMode.WORLD));
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new XPVacuumMenu(this, inventory, containerId);
    }

    @Override
    public void handleEntity(ExperienceOrb xpe) {
        int filled = getFluidTankNN().fill(new FluidStack(EIOFluids.XP_JUICE.get(), xpe.getValue() * EXPTOFLUID), FluidAction.EXECUTE);
        if (filled == xpe.getValue() * EXPTOFLUID) {
            xpe.discard();
        } else {
            xpe.value -= filled / ((float) EXPTOFLUID);
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