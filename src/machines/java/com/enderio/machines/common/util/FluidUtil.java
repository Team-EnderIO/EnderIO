package com.enderio.machines.common.util;

import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

/**
 * Helper class for dealing with Fluid related things.
 */
public class FluidUtil {
    /**
     * Gets an {@link IFluidHandlerItem} from an {@link ItemStack} or null if it's
     * not present.
     * 
     * @param itemStack {@link ItemStack} to get the {@link IFluidHandlerItem} from
     * @return An {@link IFluidHandlerItem} or null
     */
    public static IFluidHandlerItem getIFluidHandlerItem(@Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        Optional<IFluidHandlerItem> fluidHandlerCap = itemStack
                .getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        if (!fluidHandlerCap.isPresent()) {
            return null;
        }

        return fluidHandlerCap.get();
    }
}
