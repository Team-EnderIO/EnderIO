package com.enderio.machines.common.attachment;

import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface IFluidTankUser {

    MachineTankLayout getTankLayout();
    MachineFluidHandler getFluidHandler();
    MachineFluidHandler createFluidHandler();

    default void saveTank(CompoundTag pTag) {
        pTag.put(MachineNBTKeys.FLUIDS, getFluidHandler().serializeNBT());
    }

    default void loadTank(CompoundTag pTag) {
        getFluidHandler().deserializeNBT(pTag.getCompound(MachineNBTKeys.FLUIDS));
    }

    ICapabilityProvider<MachineBlockEntity, Direction, IFluidHandler> FLUID_HANDLER_PROVIDER =
        (be, side) ->{
            if (be instanceof IFluidTankUser user) {
                return user.getFluidHandler().getForSide(side);
            }
            return null;
        };
}
