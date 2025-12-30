package com.enderio.enderio.foundation.attachment;

import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.io.fluid.MachineFluidHandler;
import com.enderio.enderio.foundation.io.fluid.MachineTankLayout;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;

public interface FluidTankUser {

    MachineTankLayout getTankLayout();

    MachineFluidHandler getFluidHandler();

    MachineFluidHandler createFluidHandler();

    default void saveTank(ValueOutput output) {
        output.putChild(MachineNBTKeys.FLUIDS, getFluidHandler());
    }

    default void loadTank(ValueInput input) {
        input.child(MachineNBTKeys.FLUIDS)
            .ifPresent(f -> getFluidHandler().deserialize(f));
    }

    ICapabilityProvider<BlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be, side) -> {
        if (be instanceof FluidTankUser user) {
            return user.getFluidHandler().getForSide(side);
        }
        return null;
    };
}
