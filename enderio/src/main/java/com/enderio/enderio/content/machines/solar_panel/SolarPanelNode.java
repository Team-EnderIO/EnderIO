package com.enderio.enderio.content.machines.solar_panel;

import com.enderio.core.common.graph.INetworkNode;
import com.enderio.enderio.foundation.io.TransferUtil;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class SolarPanelNode implements INetworkNode<SolarPanelNetwork, SolarPanelNode> {
    private final SolarPanelBlockEntity blockEntity;

    private final EnergyJournal energyJournal = new EnergyJournal();
    private SolarPanelNetwork network;
    private boolean isPrimaryNode;

    private int energyStored;

    public SolarPanelNode(SolarPanelBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.network = new SolarPanelNetwork(this);
    }

    public boolean isPrimaryNode() {
        return isPrimaryNode;
    }

    public void makePrimaryNode() {
        isPrimaryNode = true;
    }

    public int getEnergyStored() {
        return energyStored;
    }

    public void setEnergyStored(int energyStored) {
        this.energyStored = energyStored;
    }

    public int getMaxEnergyStored() {
        return blockEntity.tier().getStorageCapacity();
    }

    /**
     * Lossy on purpose, adding energy by generation when we're solar just means we lose the leftovers.
     */
    private int addEnergy(int energy) {
        int energyToInsert = Math.min(getMaxEnergyStored() - getEnergyStored(), energy);
        energyStored += energyToInsert;
        blockEntity.setChanged();
        return energyToInsert;
    }

    public void addEnergyToNetwork(int energy) {
        int energyInserted = Math.min(getMaxEnergyStored() - getEnergyStored(), energy);
        energyStored += energyInserted;
        blockEntity.setChanged();

        if (energyInserted < energy) {
            for (var node : network.nodes()) {
                if (node == this) {
                    continue;
                }

                energyInserted -= node.addEnergy(energy - energyInserted);
            }
        }
    }

    public int extractEnergy(int maxExtract, TransactionContext transaction) {
        int extracted = Math.min(getEnergyStored(), maxExtract);
        if (extracted > 0) {
            energyJournal.updateSnapshots(transaction);
            energyStored -= extracted;
            return extracted;
        }

        return extracted;
    }

    public void distributeEnergy() {
        if (!isPrimaryNode) {
            throw new IllegalStateException("Cannot distribute energy from non-primary node");
        }

        try (Transaction transaction = Transaction.openRoot()) {
            Set<EnergyHandler> validTargets = network.nodes()
                .stream()
                .<EnergyHandler>mapMulti((node, consumer) ->
                    node.blockEntity.getValidPushTargets().forEach(consumer))
                .collect(Collectors.toSet());

            // Extract from the entire network
            int toExtract = TransferUtil.distributeEnergyEvenlyBetween(network.getTotalEnergyStored(), validTargets, transaction);
            for (var node : network.nodes()) {
                int extracted = node.extractEnergy(toExtract, transaction);
                toExtract -= extracted;

                if (toExtract <= 0) {
                    break;
                }
            }

            // Only commit if we removed all the energy we distributed from the network.
            if (toExtract > 0) {
                return;
            }

            transaction.commit();
        }
    }

    @Override
    public boolean isValid() {
        return network != null;
    }

    @Override
    public SolarPanelNetwork getNetwork() {
        return Objects.requireNonNull(network);
    }

    @Override
    public void setNetwork(@Nullable SolarPanelNetwork network) {
        this.network = network;
        this.isPrimaryNode = false;
    }

    private class EnergyJournal extends SnapshotJournal<Integer> {
        @Override
        protected Integer createSnapshot() {
            return energyStored;
        }

        @Override
        protected void revertToSnapshot(Integer snapshot) {
            energyStored = snapshot;
        }

        @Override
        protected void onRootCommit(Integer originalState) {
            if (originalState != energyStored) {
                blockEntity.setChanged();
            }
        }
    }
}
