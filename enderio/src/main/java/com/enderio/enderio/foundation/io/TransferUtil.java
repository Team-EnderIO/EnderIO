package com.enderio.enderio.foundation.io;

import com.enderio.enderio.api.io.IOMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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
            ItemStack extracted = from.extractItem(i, from.getSlotLimit(i), true);
            if (!extracted.isEmpty()) {
                for (int j = 0; j < to.getSlots(); j++) {
                    ItemStack remainder = to.insertItem(j, extracted, false);

                    int successfullyMoved = extracted.getCount() - remainder.getCount();
                    if (successfullyMoved > 0) {
                        from.extractItem(i, extracted.getCount() - remainder.getCount(), false);
                    }

                    // If there is no remainder, take from the next "from" slot.
                    if (remainder.getCount() <= 0) {
                        break;
                    }
                }
            }
        }
    }

    // endregion

    // region Fluids

    // TODO: Possibly raise this too?
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
            int filled = 0;
            for (int i = 0; i < selfItemHandler.getTanks(); i++) {
                filled += FluidUtil.tryFluidTransfer(otherItemHandler, selfItemHandler, new FluidStack(selfItemHandler.getFluidInTank(i).getFluid(), maxDrain), true).getAmount();
            }
            if (filled > 0) {
                return;
            }
        }

        if (canPull) {
            for (int i = 0; i < selfItemHandler.getTanks(); i++) {
                if (selfItemHandler.getFluidInTank(i).isEmpty()) {
                    FluidUtil.tryFluidTransfer(selfItemHandler, otherItemHandler, maxDrain, true);
                } else {
                    FluidUtil.tryFluidTransfer(selfItemHandler, otherItemHandler, new FluidStack(selfItemHandler.getFluidInTank(i).getFluid(), maxDrain), true);
                }
            }
        }
    }

    // endregion

    // region Even Distribution

    // Helper record for energy distribution
    private record EnergyStoragePair(IEnergyStorage self, IEnergyStorage receiver) {}

    /**
     * Distributes energy evenly to all neighboring blocks that can receive it.
     * Queries capabilities per-side for both source and receivers.
     *
     * @apiNote Assumes that the source block has a single energy buffer shared across all sides. If this is not the case, do not use this.
     * @param level The level
     * @param pos The position of the source block
     * @param canPushTo Function to check if energy can be pushed to a given direction
     */
    public static void distributeEnergyEvenly(Level level, BlockPos pos, Function<Direction, Boolean> canPushTo) {
        // Collect all valid receivers and senders per side
        List<EnergyStoragePair> transfers = new ArrayList<>();
        
        for (Direction direction : Direction.values()) {
            if (!canPushTo.apply(direction)) {
                continue;
            }

            // Get self capability for this side
            IEnergyStorage selfHandler = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction);
            if (selfHandler == null || !selfHandler.canExtract()) {
                continue;
            }

            // Get neighbor capability
            IEnergyStorage otherHandler = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if (otherHandler != null && otherHandler != selfHandler && otherHandler.canReceive()) {
                transfers.add(new EnergyStoragePair(selfHandler, otherHandler));
            }
        }

        // Abort if we have no valid pairs
        if (transfers.isEmpty()) {
            return;
        }

        // Use first available self handler to check total available energy
        // all of the 'self' handlers should be the same buffer.
        int availableEnergy = transfers.getFirst().self.extractEnergy(Integer.MAX_VALUE, true);
        if (availableEnergy <= 0) {
            return;
        }

        // Distribute evenly using the same algorithm as energy conduits
        int energyRemaining = availableEnergy;
        int toShareWith = transfers.size();
        
        for (EnergyStoragePair transfer : transfers) {
            // If we have too little energy left, just give it to the first handler that will accept it all
            int shareAmount;
            if (energyRemaining <= toShareWith) {
                shareAmount = energyRemaining;
            } else {
                shareAmount = energyRemaining / toShareWith;
            }

            int inserted = transfer.receiver.receiveEnergy(shareAmount, false);
            if (inserted > 0) {
                transfer.self.extractEnergy(inserted, false);
                energyRemaining -= inserted;
            }
            
            toShareWith--;
            if (energyRemaining <= 0) {
                break;
            }
        }
    }

    // endregion
}
