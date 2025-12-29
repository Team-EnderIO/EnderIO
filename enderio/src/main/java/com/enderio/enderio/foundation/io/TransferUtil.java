package com.enderio.enderio.foundation.io;

import com.enderio.enderio.api.io.IOMode;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;

// TODO: This should probably have unit tests.
public class TransferUtil {

    // TODO: This should possibly allow transactions to be passed in.

    // region Items

    public static void distributeItems(IOMode mode, ResourceHandler<ItemResource> selfItemHandler, ResourceHandler<ItemResource> otherItemHandler) {
        distributeItems(mode.canPush(), mode.canPull(), selfItemHandler, otherItemHandler);
    }

    public static void distributeItems(boolean canPush, boolean canPull, ResourceHandler<ItemResource> selfItemHandler, ResourceHandler<ItemResource> otherItemHandler) {
        // TODO: Check that this is correct.
        if (canPush) {
            ResourceHandlerUtil.move(selfItemHandler, otherItemHandler, ir -> true, Integer.MAX_VALUE, null);
        }

        if (canPull) {
            ResourceHandlerUtil.move(otherItemHandler, selfItemHandler, ir -> true, Integer.MAX_VALUE, null);
        }
    }

    // endregion

    // region Fluids

    // TODO: Possibly raise this too?
    public static final int DEFAULT_FLUID_DRAIN = 100;

    public static void distributeFluids(IOMode mode, ResourceHandler<FluidResource> selfItemHandler, ResourceHandler<FluidResource> otherItemHandler) {
        distributeFluids(mode.canPush(), mode.canPull(), selfItemHandler, otherItemHandler, DEFAULT_FLUID_DRAIN);
    }

    public static void distributeFluids(IOMode mode, ResourceHandler<FluidResource> selfItemHandler, ResourceHandler<FluidResource> otherItemHandler, int maxDrain) {
        distributeFluids(mode.canPush(), mode.canPull(), selfItemHandler, otherItemHandler, maxDrain);
    }

    public static void distributeFluids(boolean canPush, boolean canPull, ResourceHandler<FluidResource> selfItemHandler, ResourceHandler<FluidResource> otherItemHandler) {
        distributeFluids(canPush, canPull, selfItemHandler, otherItemHandler, DEFAULT_FLUID_DRAIN);
    }

    public static void distributeFluids(boolean canPush, boolean canPull, ResourceHandler<FluidResource> selfItemHandler, ResourceHandler<FluidResource> otherItemHandler, int maxDrain) {
        // TODO: Do we want to imitate old behaviour where if we have no fluid, we pull by default?

        // TODO: 1.21.11: Check this is right.
        if (canPush) {
            ResourceHandlerUtil.move(selfItemHandler, otherItemHandler, fr -> true, maxDrain, null);
        }

        if (canPull) {
            ResourceHandlerUtil.move(otherItemHandler, selfItemHandler, fr -> true, maxDrain, null);
        }
    }

    // endregion
}
