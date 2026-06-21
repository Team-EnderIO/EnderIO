package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.core.common.graph.Network;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.foundation.network.packets.ClientBoundRemoveCapacitorBankPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Iterator;
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
        long total = 0;

        for (CapacitorBankNode node : nodes()) {
            total += node.getEnergyStored();
        }

        return total;
    }

    public long getTotalMaxEnergyStored() {
        long total = 0;

        for (CapacitorBankNode node : nodes()) {
            total += node.getMaxEnergyStored();
        }

        return total;
    }

    public List<BlockPos> positions() {
        List<BlockPos> positions = new ArrayList<>(nodes().size());

        for (CapacitorBankNode node : nodes()) {
            positions.add(node.getPos());
        }

        return positions;
    }

    public long getAddedEnergy() {
        long sum = 0;

        for (CapacitorBankNode node : nodes()) {
            sum += node.getAdded();
        }

        return sum;
    }

    public void reset(long time) {
        for (CapacitorBankNode node : nodes()) {
            node.reset(time);
        }
    }

    public long getSendEnergy() {
        long sum = 0;

        for (CapacitorBankNode node : nodes()) {
            sum += node.getSend();
        }

        return sum;
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
            Iterator<CapacitorBankNode> it = nodes().iterator();
            if (it.hasNext()) {
                it.next().makePrimaryNode();
            }
        }
    }

    @Override
    protected void onMerged(CapacitorBankNetwork other) {
        Iterator<CapacitorBankNode> it = other.nodes().iterator();

        if (it.hasNext()) {
            CapacitorBankNode n = it.next();

            if (n.getBlockEntity().getLevel() instanceof ServerLevel level) {
                PacketDistributor.sendToPlayersTrackingChunk(level, new ChunkPos(n.getPos()),
                    new ClientBoundRemoveCapacitorBankPacket(n.getNetwork().getUuid())
                );
            }
        }

        other.uuid = this.uuid;
        other.markDirty();
        super.onMerged(other);
    }

    @Override
    protected void onGraphSplit(Set<CapacitorBankNetwork> newGraphs) {
        super.onGraphSplit(newGraphs);
        for (CapacitorBankNetwork n : newGraphs) {
            n.uuid = UUID.randomUUID();

            Iterator<CapacitorBankNode> it = n.nodes().iterator();
            if (it.hasNext()) {
                it.next().makePrimaryNode();
            }

            n.markDirty();
        }
    }

    public RedstoneControl getRedstoneControl() {
        return redstoneControl;
    }

    public void setRedstoneControl(RedstoneControl redstoneControl) {
        this.redstoneControl = redstoneControl;
        for (CapacitorBankNode n : nodes()) {
            n.getBlockEntity().setRedstoneControl(redstoneControl);
        }
    }

    public boolean isRedstoneBlocked() {
        return switch (redstoneControl) {
            case ALWAYS_ACTIVE -> false;
            case ACTIVE_WITH_SIGNAL -> {
                for (CapacitorBankNode n : nodes()) {
                    if (!n.getBlockEntity().isRedstoneBlocked()) {
                        yield false;
                    }
                }
                yield true;
            }
            case ACTIVE_WITHOUT_SIGNAL -> {
                for (CapacitorBankNode n : nodes()) {
                    if (n.getBlockEntity().isRedstoneBlocked()) {
                        yield true;
                    }
                }
                yield false;
            }
            case NEVER_ACTIVE -> true;
        };
    }
}
