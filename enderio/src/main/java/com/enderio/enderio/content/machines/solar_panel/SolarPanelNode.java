package com.enderio.enderio.content.machines.solar_panel;

import com.enderio.core.common.graph.INetworkNode;
import com.enderio.enderio.foundation.io.TransferUtil;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class SolarPanelNode implements INetworkNode<SolarPanelNetwork, SolarPanelNode> {
    private final SolarPanelBlockEntity blockEntity;
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

    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(getEnergyStored(), maxExtract);
        if (!simulate) {
            this.energyStored = Math.max(getEnergyStored() - energyExtracted, 0);
            blockEntity.setChanged();
        }

        return energyExtracted;
    }

    public void distributeEnergy() {
        if (!isPrimaryNode) {
            throw new IllegalStateException("Cannot distribute energy from non-primary node");
        }

        Set<IEnergyStorage> validTargets = network.nodes()
            .stream()
            .<IEnergyStorage>mapMulti((node, consumer) ->
                node.blockEntity.getValidPushTargets().forEach(consumer))
            .collect(Collectors.toSet());

        int transferred = TransferUtil.distributeEnergyEvenlyBetween(network.getTotalEnergyStored(), validTargets);

        // Extract from the entire network
        int toExtract = transferred;
        for (var node : network.nodes()) {
            int extracted = node.extractEnergy(toExtract, false);
            toExtract -= extracted;

            if (toExtract <= 0) {
                break;
            }
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
}
