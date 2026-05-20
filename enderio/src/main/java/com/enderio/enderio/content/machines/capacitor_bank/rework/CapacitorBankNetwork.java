package com.enderio.enderio.content.machines.capacitor_bank.rework;

import com.enderio.core.common.graph.Network;
import com.enderio.enderio.api.io.RedstoneControl;
import net.minecraft.core.BlockPos;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CapacitorBankNetwork extends Network<CapacitorBankNetwork, CapacitorBankNode> {

    private UUID uuid = UUID.randomUUID();
    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;

    public CapacitorBankNetwork(CapacitorBankNode initialNode) {
        super(initialNode);

        initialNode.makePrimaryNode();
    }

    public CapacitorBankNetwork() {
        super();
    }

    @Override
    protected CapacitorBankNetwork createEmpty() {
        return new CapacitorBankNetwork();
    }

    public long getTotalEnergyStored() {
        return nodes().stream().mapToLong(CapacitorBankNode::getEnergyStored).sum();
    }

    public long getTotalMaxEnergyStored() {
        return nodes().stream().mapToLong(CapacitorBankNode::getMaxEnergyStored).sum();
    }

    public List<BlockPos> positions() {
        return nodes().stream().map(CapacitorBankNode::getPos).toList();
    }

    public long getAddedEnergy() {
        return nodes().stream().mapToLong(CapacitorBankNode::getAdded).sum();
    }

    public void reset(long time) {
        nodes().forEach(n -> n.reset(time));
    }

    public long getSendEnergy() {
        return nodes().stream().mapToLong(CapacitorBankNode::getSend).sum();
    }

    public void markDirty() {
        nodes().forEach(CapacitorBankNode::markDirty);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    protected void onNodeRemoved(CapacitorBankNode node) {
        super.onNodeRemoved(node);

        // If we removed the 'primary', find a new one.
        if (node.isPrimaryNode()) {
            nodes().stream().findFirst().ifPresent(CapacitorBankNode::makePrimaryNode);
        }
    }

    @Override
    protected void onMerged(CapacitorBankNetwork other) {
        super.onMerged(other);
        other.uuid = this.uuid;
        other.markDirty();
    }

    @Override
    protected void onGraphSplit(Set<CapacitorBankNetwork> newGraphs) {
        super.onGraphSplit(newGraphs);
        newGraphs.stream().findFirst().ifPresent(n -> {
            n.uuid = UUID.randomUUID();
            n.markDirty();
        });
    }

    public RedstoneControl getRedstoneControl() {
        return redstoneControl;
    }

    public void setRedstoneControl(RedstoneControl redstoneControl) {
        this.redstoneControl = redstoneControl;
        nodes().forEach(n -> n.getBlockEntity().setRedstoneControl(redstoneControl));
    }

    public boolean isRedstoneBlocked() {
        return switch (redstoneControl) {
            case ALWAYS_ACTIVE -> false;
            case ACTIVE_WITH_SIGNAL -> nodes().stream().allMatch(n -> n.getBlockEntity().isRedstoneBlocked());
            case ACTIVE_WITHOUT_SIGNAL -> nodes().stream().anyMatch(n -> n.getBlockEntity().isRedstoneBlocked());
            case NEVER_ACTIVE -> true;
        };
    }
}
