package com.enderio.enderio.foundation.io;

import com.enderio.enderio.api.io.IOMode;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

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

    public static void distributeFluids(IOMode mode, ResourceHandler<FluidResource> selfItemHandler, ResourceHandler<FluidResource> otherItemHandler) {
        distributeFluids(mode.canPush(), mode.canPull(), selfItemHandler, otherItemHandler);
    }

    public static void distributeFluids(boolean canPush, boolean canPull, ResourceHandler<FluidResource> selfItemHandler, ResourceHandler<FluidResource> otherItemHandler) {
        // TODO: Do we want to imitate old behaviour where if we have no fluid, we pull by default?

        // TODO: 1.21.11: Check this is right.
        if (canPush) {
            ResourceHandlerUtil.move(selfItemHandler, otherItemHandler, _ -> true, Integer.MAX_VALUE, null);
        }

        if (canPull) {
            ResourceHandlerUtil.move(otherItemHandler, selfItemHandler, _ -> true, Integer.MAX_VALUE, null);
        }
    }

    // endregion

    // region Even Distribution

    // Helper record for energy distribution
    private record EnergyHandlerPair(EnergyHandler self, EnergyHandler receiver) {}

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
        List<EnergyHandlerPair> transfers = new ArrayList<>();
        
        for (Direction direction : Direction.values()) {
            if (!canPushTo.apply(direction)) {
                continue;
            }

            // Get self capability for this side
            EnergyHandler selfHandler = level.getCapability(Capabilities.Energy.BLOCK, pos, direction);
            if (selfHandler == null) {
                continue;
            }

            // Get neighbor capability
            EnergyHandler otherHandler = level.getCapability(Capabilities.Energy.BLOCK, pos.relative(direction), direction.getOpposite());
            if (otherHandler != null && otherHandler != selfHandler) {
                transfers.add(new EnergyHandlerPair(selfHandler, otherHandler));
            }
        }

        // Abort if we have no valid pairs
        if (transfers.isEmpty()) {
            return;
        }

        try (Transaction transaction = Transaction.openRoot()) {
            // Use first available self handler to check total available energy
            // all of the 'self' handlers should be the same buffer.
            int availableEnergy;
            try (Transaction checkTransaction = Transaction.open(transaction)) {
                availableEnergy = transfers.getFirst().self.extract(Integer.MAX_VALUE, checkTransaction);
            }

            if (availableEnergy <= 0) {
                return;
            }

            // Distribute evenly using the same algorithm as energy conduits
            int energyRemaining = availableEnergy;
            int toShareWith = transfers.size();

            for (EnergyHandlerPair transfer : transfers) {
                // If we have too little energy left, just give it to the first handler that will accept it all
                int shareAmount;
                if (energyRemaining <= toShareWith) {
                    shareAmount = energyRemaining;
                } else {
                    shareAmount = energyRemaining / toShareWith;
                }

                int inserted = EnergyHandlerUtil.move(transfer.self, transfer.receiver, shareAmount, transaction);
                if (inserted > 0) {
                    energyRemaining -= inserted;
                }

                toShareWith--;
                if (energyRemaining <= 0) {
                    break;
                }
            }

            transaction.commit();
        }
    }

    public static int distributeEnergyEvenlyBetween(int availableEnergy, Set<IEnergyStorage> receivers) {
        // Abort if we have no valid pairs
        if (receivers.isEmpty()) {
            return 0;
        }

        // Distribute evenly
        int energyRemaining = availableEnergy;
        int toShareWith = receivers.size();

        for (IEnergyStorage receiver : receivers) {
            // If we have too little energy left, just give it to the first handler that will accept it all
            int shareAmount;
            if (energyRemaining <= toShareWith) {
                shareAmount = energyRemaining;
            } else {
                shareAmount = energyRemaining / toShareWith;
            }

            int inserted = receiver.receiveEnergy(shareAmount, false);
            energyRemaining -= inserted;

            toShareWith--;
            if (energyRemaining <= 0) {
                break;
            }
        }

        return availableEnergy - energyRemaining;
    }

    // endregion
}
