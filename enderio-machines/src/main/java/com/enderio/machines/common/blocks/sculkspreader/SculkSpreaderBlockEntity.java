package com.enderio.machines.common.blocks.sculkspreader;

import com.enderio.base.api.io.IOMode;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class SculkSpreaderBlockEntity extends MachineBlockEntity implements FluidTankUser {
    public static final SingleSlotAccess INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final TankAccess TANK = new TankAccess();
    private final MachineFluidHandler fluidHandler;
    private final SculkSpreader sculkSpreader = new SculkSpreader(false, BlockTags.SCULK_REPLACEABLE, 10, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);

    public SculkSpreaderBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.SCULK_SPREADER.get(), worldPosition, blockState, false);
        this.fluidHandler = createFluidHandler();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PULL);
    }

    @Override
    protected @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot((i,s) -> s.getCapability(EIOCapabilities.SoulHandler.ITEM) != null)
            .slotAccess(INPUT)
            .outputSlot()
            .slotAccess(OUTPUT)
            .build();
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return MachineTankLayout.builder().tank(TANK, 1000, f -> f.is(EIOTags.Fluids.EXPERIENCE)).build();
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct(5) && !getFluidTank().isEmpty()){
            FluidStack drained = getFluidTank().drain(100, IFluidHandler.FluidAction.EXECUTE);
            if (!drained.isEmpty()) {
                sculkSpreader.addCursors(worldPosition, drained.getAmount());
            }
            sculkSpreader.updateCursors(level, worldPosition, level.random, false);
        }
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }
        };
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new SculkSpreaderMenu(i, inventory, this);
    }
}
