package com.enderio.machines.common.attachment;

import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public interface FluidTankUser {

    MachineTankLayout getTankLayout();

    MachineFluidHandler getFluidHandler();

    MachineFluidHandler createFluidHandler();

    default void saveTank(HolderLookup.Provider lookupProvider, CompoundTag pTag) {
        pTag.put(MachineNBTKeys.FLUIDS, getFluidHandler().serializeNBT(lookupProvider));
    }

    default void loadTank(HolderLookup.Provider lookupProvider, CompoundTag pTag) {
        getFluidHandler().deserializeNBT(lookupProvider, pTag.getCompound(MachineNBTKeys.FLUIDS));
    }

    ICapabilityProvider<BlockEntity, Direction, IFluidHandler> FLUID_HANDLER_PROVIDER = (be, side) -> {
        if (be instanceof FluidTankUser user) {
            return user.getFluidHandler().getForSide(side);
        }
        return null;
    };
}
