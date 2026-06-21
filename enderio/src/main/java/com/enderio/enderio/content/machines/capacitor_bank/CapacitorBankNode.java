package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.core.common.graph.INetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CapacitorBankNode implements INetworkNode<CapacitorBankNetwork, CapacitorBankNode> {

    private final CapacitorBankBlockEntity blockEntity;
    private CapacitorBankNetwork network;
    private boolean isPrimaryNode;

    private int energyStored;
    private long added;
    private long send;
    private long lastSync;

    public CapacitorBankNode(CapacitorBankBlockEntity blockEntity) {
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

    public long getAdded() {
        return added;
    }

    public long getSend() {
        return send;
    }

    public void reset(long time) {
        if (lastSync != time) {
            send = 0;
            added = 0;
            lastSync = time;
        }
    }

    public CapacitorBankBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = extractEnergyInternal(maxExtract, simulate);
        if (!simulate) {
            this.send += energyExtracted;
        }
        return energyExtracted;
    }

    //TODO work around energy display
    private int extractEnergyInternal(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(getEnergyStored(), maxExtract);
        if (!simulate) {
            this.energyStored = Math.max(getEnergyStored() - energyExtracted, 0);
            blockEntity.setChanged();
        }

        return energyExtracted;
    }

    public int insertEnergy(int maxInsert, boolean simulate) {
        int energyInserted = insertEnergyInternal(maxInsert, simulate);
        if (!simulate) {
            this.added += energyInserted;
            blockEntity.setChanged();
        }

        return energyInserted;
    }

    //TODO work around energy display
    private int insertEnergyInternal(int maxInsert, boolean simulate) {
        int energyInserted = Mth.clamp(maxInsert, 0, getMaxEnergyStored() - getEnergyStored());
        if (!simulate) {
            this.energyStored = Math.min(getEnergyStored() + energyInserted, getMaxEnergyStored());
            blockEntity.setChanged();
        }

        return energyInserted;
    }

    //Since the energy added used to be in all nodes, there's no need to have a remaining energy
    private void reDistributeNodes(long availableEnergy, List<CapacitorBankNode> receivers) {
        // Abort if we have no valid pairs
        if (receivers.isEmpty()) {
            return;
        }

        // Distribute evenly
        long energyRemaining = availableEnergy;
        int toShareWith = receivers.size();

        for (CapacitorBankNode receiver : receivers) {
            // If we have too little energy left, just give it to the first handler that will accept it all
            long shareAmount;
            if (energyRemaining <= toShareWith) {
                shareAmount = energyRemaining;
            } else {
                shareAmount = energyRemaining / toShareWith;
            }

            //Because every node has less than INT.MAX energy, shareAmount should always be an int
            int inserted = receiver.insertEnergyInternal((int) shareAmount, false);
            energyRemaining -= inserted;

            toShareWith--;
            if (energyRemaining <= 0) {
                break;
            }
        }
    }

    private static long distributeEnergyEvenlyBetween(long availableEnergy, List<IEnergyStorage> receivers) {
        // Abort if we have no valid pairs
        if (receivers.isEmpty()) {
            return 0;
        }

        // Distribute evenly
        long energyRemaining = availableEnergy;
        int toShareWith = receivers.size();

        for (IEnergyStorage receiver : receivers) {
            // If we have too little energy left, just give it to the first handler that will accept it all
            long shareAmount;
            if (energyRemaining <= toShareWith) {
                shareAmount = energyRemaining;
            } else {
                shareAmount = energyRemaining / toShareWith;
            }

            int inserted = receiver.receiveEnergy(Math.clamp(shareAmount, 0, Integer.MAX_VALUE), false);
            energyRemaining -= inserted;

            toShareWith--;
            if (energyRemaining <= 0) {
                break;
            }
        }

        return availableEnergy - energyRemaining;
    }

    public void distributeEnergy() {
        if (!isPrimaryNode) {
            throw new IllegalStateException("Cannot distribute energy from non-primary node");
        }

        if (network.isRedstoneBlocked()) {
            return; //Don't do anything if redstone is blocked
        }

        //Sorted list, so we push to the emptiest first
        List<IEnergyStorage> validTargets = new ArrayList<>();

        for (CapacitorBankNode node : network.nodes()) {
            validTargets.addAll(node.blockEntity.getValidPushTargets());
        }

        validTargets.sort(Comparator.comparingInt(
            c -> c.getMaxEnergyStored() - c.getEnergyStored()
        ));

        // Extract from the entire network
        long total = 0L;
        for (var node : network.nodes()) {
            total += node.energyStored;
            node.energyStored = 0;
        }

        //TODO long support
        //first push outside
        long transferred = distributeEnergyEvenlyBetween(total, validTargets);
        this.send += transferred;
        long remaining = total - transferred;
        if (remaining > 0) {
            // then balance the nodes
            // first sort to fill the smallest ones first
            ArrayList<CapacitorBankNode> nodes = new ArrayList<>(network.nodes());
            nodes.sort(Comparator.comparingInt(c -> c.getMaxEnergyStored() - c.getEnergyStored()));
            reDistributeNodes(remaining, nodes);
        }
    }

    public BlockPos getPos() {
        return blockEntity.getBlockPos();
    }

    public void markDirty() {
        blockEntity.setChanged();
        blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_ALL);
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
        if (network != null) {
            this.isPrimaryNode = false;
        }
    }
}
