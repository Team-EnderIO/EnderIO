package com.enderio.enderio.foundation.attachment;

import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.io.fluid.MachineFluidHandler;
import com.enderio.enderio.foundation.io.fluid.MachineTankLayout;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.IFluidHandler;

public interface FluidTankUser {

    MachineTankLayout getTankLayout();

    MachineFluidHandler getFluidHandler();

    MachineFluidHandler createFluidHandler();

    default void saveTank(CompoundTag tag) {
        tag.put(MachineNBTKeys.FLUIDS, getFluidHandler().serializeNBT());
    }

    default void loadTank(CompoundTag tag) {
        getFluidHandler().deserializeNBT(tag.getCompound(MachineNBTKeys.FLUIDS));
    }

    ICapabilityProvider FLUID_HANDLER_PROVIDER = (be, side) -> {
        if (be instanceof FluidTankUser user) {
            return user.getFluidHandler().getForSide(side);
        }
        return null;
    };
}
