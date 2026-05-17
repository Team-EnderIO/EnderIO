package com.enderio.enderio.content.machines.capacitor_bank.rework;

import com.enderio.core.common.graph.INetworkNode;
import com.enderio.enderio.foundation.io.TransferUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CapacitorBankNode implements INetworkNode<CapacitorBankNetwork, CapacitorBankNode> {

    private final NewCapacitorBankBlockEntity blockEntity;
    private CapacitorBankNetwork network;
    private boolean isPrimaryNode;

    private int energyStored;

    public CapacitorBankNode(NewCapacitorBankBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.network = new CapacitorBankNetwork(this);
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
        return blockEntity.getTier().getStorageCapacity();
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(getEnergyStored(), maxExtract);
        if (!simulate) {
            this.energyStored = Math.max(getEnergyStored() - energyExtracted, 0);
            blockEntity.setChanged();
        }

        return energyExtracted;
    }

    public int insertEnergy(int maxInsert, boolean simulate) {
        int energyInserted = Mth.clamp(maxInsert, 0, getMaxEnergyStored() - getEnergyStored());
        if (!simulate) {
            this.energyStored = Math.min(getEnergyStored() + energyInserted, getMaxEnergyStored());
            blockEntity.setChanged();
        }

        return energyInserted;
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

        //TODO long support
        int transferred = TransferUtil.distributeEnergyEvenlyBetween((int) network.getTotalEnergyStored(), validTargets);

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

    public BlockPos getPos() {
        return blockEntity.getBlockPos();
    }

    public void markDirty() {
        blockEntity.setChanged();
    }

    @Override
    public boolean isValid() {
        return network != null;
    }

    @Override
    public CapacitorBankNetwork getNetwork() {
        return Objects.requireNonNull(network);
    }

    @Override
    public void setNetwork(@Nullable CapacitorBankNetwork network) {
        this.network = network;
        this.isPrimaryNode = false;
    }
}
