package com.enderio.core.common.capability;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class EnderFluidUtil {
    public static boolean isEmpty(IFluidHandler fluidHandler) {
        for (int i = 0; i < fluidHandler.getTanks(); i++) {
            if (fluidHandler.getFluidInTank(i).getAmount() >= fluidHandler.getTankCapacity(i)) {
                return false;
            }
        }

        return true;
    }

    public static boolean isFull(IFluidHandler fluidHandler) {
        for (int i = 0; i < fluidHandler.getTanks(); i++) {
            if (fluidHandler.getFluidInTank(i).getAmount() < fluidHandler.getTankCapacity(i)) {
                return false;
            }
        }

        return true;
    }
}
