package com.enderio.machines.common.io;

import com.enderio.api.io.IOMode;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

public class TransferUtil {

    // region Items

    public static void distributeItems(IOMode mode, IItemHandler selfItemHandler, IItemHandler otherItemHandler) {
        distributeItems(mode.canPush(), mode.canPull(), selfItemHandler, otherItemHandler);
    }

    public static void distributeItems(boolean canPush, boolean canPull, IItemHandler selfItemHandler, IItemHandler otherItemHandler) {
        if (canPush) {
            moveItems(selfItemHandler, otherItemHandler);
        }

        if (canPull) {
            moveItems(otherItemHandler, selfItemHandler);
        }
    }

    private static void moveItems(IItemHandler from, IItemHandler to) {
        for (int i = 0; i < from.getSlots(); i++) {
            ItemStack extracted = from.extractItem(i, 1, true);
            if (!extracted.isEmpty()) {
                for (int j = 0; j < to.getSlots(); j++) {
                    ItemStack inserted = to.insertItem(j, extracted, false);
                    if (inserted.isEmpty()) {
                        from.extractItem(i, 1, false);
                        return;
                    }
                }
            }
        }
    }

    // endregion

    // region Fluids

    public static final int DEFAULT_FLUID_DRAIN = 100;

    public static void distributeFluids(IOMode mode, IFluidHandler selfItemHandler, IFluidHandler otherItemHandler) {
        distributeFluids(mode.canPush(), mode.canPull(), selfItemHandler, otherItemHandler, DEFAULT_FLUID_DRAIN);
    }

    public static void distributeFluids(IOMode mode, IFluidHandler selfItemHandler, IFluidHandler otherItemHandler, int maxDrain) {
        distributeFluids(mode.canPush(), mode.canPull(), selfItemHandler, otherItemHandler, maxDrain);
    }

    public static void distributeFluids(boolean canPush, boolean canPull, IFluidHandler selfItemHandler, IFluidHandler otherItemHandler) {
        distributeFluids(canPush, canPull, selfItemHandler, otherItemHandler, DEFAULT_FLUID_DRAIN);
    }

    public static void distributeFluids(boolean canPush, boolean canPull, IFluidHandler selfItemHandler, IFluidHandler otherItemHandler, int maxDrain) {
        // TODO: Do we want to imitate old behaviour where if we have no fluid, we pull by default?

        if (canPush) {
            FluidUtil.tryFluidTransfer(selfItemHandler, otherItemHandler, maxDrain, true);
        }

        if (canPull) {
            FluidUtil.tryFluidTransfer(otherItemHandler, selfItemHandler, maxDrain, true);
        }
    }

    // endregion
}
