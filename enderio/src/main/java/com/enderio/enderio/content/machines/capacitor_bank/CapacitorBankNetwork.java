package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.core.common.graph.Network;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.content.conduits.network.ConduitNetworkImpl;
import com.enderio.enderio.content.conduits.network.ConduitNodeImpl;
import com.enderio.enderio.foundation.network.packets.ClientBoundRemoveCapacitorBankPacket;
import com.enderio.enderio.foundation.network.packets.ClientBoundSyncCapacitorBankPacket;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class CapacitorBankNetwork extends Network<CapacitorBankNetwork, CapacitorBankNode> {

    public static final int AVERAGE_IO_OVER_X_TICKS = 10;

    public static final Codec<CapacitorBankNetwork> CODEC = RecordCodecBuilder.create(i ->
        i.group(Codec.LONG.fieldOf("energy").forGetter(c -> c.totalEnergyStored),
                RedstoneControl.CODEC.fieldOf("redstone").forGetter(c -> c.redstoneControl))
            .and(graphCodec(i, CapacitorBankNode.CODEC))
            .apply(i, CapacitorBankNetwork::new));

    private UUID uuid = UUID.randomUUID();
    private RedstoneControl redstoneControl = RedstoneControl.ALWAYS_ACTIVE;

    private long totalEnergyStored;
    private long totalMaxEnergyStored;
    private List<BlockPos> positions = new ArrayList<>();
    private long lastSync;

    private final Map<SidedPos, Long> energyIO = new HashMap<>();

    public CapacitorBankNetwork(CapacitorBankNode initialNode) {
        super(initialNode);
        totalMaxEnergyStored = initialNode.getMaxEnergyStored();
        this.positions.add(initialNode.getPos());
    }

    public CapacitorBankNetwork(long totalEnergyStored, RedstoneControl redstone, List<CapacitorBankNode> nodes, IndexedEdgeList edges) {
        super(nodes, edges);
        this.totalEnergyStored = totalEnergyStored;
        this.redstoneControl = redstone;
        this.positions.addAll(nodes.stream().map(CapacitorBankNode::getPos).toList());
    }

    public CapacitorBankNetwork() {
        super();
    }

    @Override
    protected CapacitorBankNetwork createEmpty() {
        return new CapacitorBankNetwork();
    }

    public long getTotalEnergyStored() {
        return totalEnergyStored;
    }

    public long getTotalMaxEnergyStored() {
        return totalMaxEnergyStored;
    }

    public List<BlockPos> positions() {
        return positions;
    }

    public long getAddedEnergy() {
        long added = 0;
        for (long io : energyIO.values()) {
            if (io > 0) {
                added += io;
            }
        }
        return added;
    }

    public long getSendEnergy() {
        long send = 0;
        for (long io : energyIO.values()) {
            if (io < 0) {
                send -= io;
            }
        }
        return send;
    }

    public void reset(long time) {
        if (lastSync != time) {
            lastSync = time;
            energyIO.clear();
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
                    if (n.getBlockEntity() != null && !n.getBlockEntity().isRedstoneBlocked()) {
                        yield false;
                    }
                }
                yield true;
            }
            case ACTIVE_WITHOUT_SIGNAL -> {
                for (CapacitorBankNode n : nodes()) {
                    if (n.getBlockEntity() != null && n.getBlockEntity().isRedstoneBlocked()) {
                        yield true;
                    }
                }
                yield false;
            }
            case NEVER_ACTIVE -> true;
        };
    }

    public int receiveEnergy(BlockPos pos, @Nullable Direction side, int amount, boolean simulate) {
        long energyBefore = getTotalEnergyStored();
        long energyAfter = Math.min(energyBefore + amount, getTotalMaxEnergyStored());
        int result = Math.toIntExact(energyAfter - energyBefore);

        if (!simulate) {
            this.totalEnergyStored = energyAfter;
            if (side != null) {
                long io = energyIO.getOrDefault(new SidedPos(pos, side), 0L);
                energyIO.put(new SidedPos(pos, side), io + result);
            }
        }

        return result;
    }

    public int extractEnergy(BlockPos pos, @Nullable Direction side, int amount, boolean simulate) {
        long extracted = Math.min(getTotalEnergyStored(), amount);

        if (!simulate) {
            this.totalEnergyStored -= extracted;
            if (side != null) {
                long io = energyIO.getOrDefault(new SidedPos(pos, side), 0L);
                energyIO.put(new SidedPos(pos, side), io - extracted);
            }
        }

        return Math.toIntExact(extracted);
    }

    public int getEnergyForNode(CapacitorTier tier) {
        long energy = getTotalEnergyStored() / (nodes().size() + 1);
        int toExtract = Math.toIntExact(Math.min(tier.getStorageCapacity(), energy));
        this.totalEnergyStored -= toExtract;
        return toExtract;
    }

    public void tick(ServerLevel serverLevel) {
        distributeEnergy();

        if (serverLevel.getGameTime() % AVERAGE_IO_OVER_X_TICKS == 0) {
            PacketDistributor.sendToPlayersInDimension(serverLevel,
                new ClientBoundSyncCapacitorBankPacket(this.getUuid(), this.getTotalEnergyStored(),
                    this.getTotalMaxEnergyStored(), this.getAddedEnergy()  / AVERAGE_IO_OVER_X_TICKS,
                    this.getSendEnergy() / AVERAGE_IO_OVER_X_TICKS, this.positions()));
            this.reset(serverLevel.getGameTime());
        }
    }

    private long distributeEnergyEvenlyBetween(long availableEnergy, List<CapacitorBankBlockEntity.SidedEnergy> receivers) {
        // Abort if we have no valid pairs
        if (receivers.isEmpty()) {
            return 0;
        }

        // Distribute evenly
        long energyRemaining = availableEnergy;
        int toShareWith = receivers.size();

        for (var sided : receivers) {
            IEnergyStorage receiver = sided.storage();
            // If we have too little energy left, just give it to the first handler that will accept it all
            long shareAmount;
            if (energyRemaining <= toShareWith) {
                shareAmount = energyRemaining;
            } else {
                shareAmount = energyRemaining / toShareWith;
            }

            int inserted = receiver.receiveEnergy(Math.clamp(shareAmount, 0, Integer.MAX_VALUE), false);
            long io = this.energyIO.getOrDefault(sided.sidedPos(), 0L);
            this.energyIO.put(sided.sidedPos(), io - inserted);
            energyRemaining -= inserted;

            toShareWith--;
            if (energyRemaining <= 0) {
                break;
            }
        }

        return availableEnergy - energyRemaining;
    }

    public void distributeEnergy() {

        if (isRedstoneBlocked()) {
            return; //Don't do anything if redstone is blocked
        }

        //Sorted list, so we push to the emptiest first
        List<CapacitorBankBlockEntity.SidedEnergy> validTargets = new ArrayList<>();

        for (CapacitorBankNode node : nodes()) {
            if (node.getBlockEntity() != null) {
                validTargets.addAll(node.getBlockEntity().getValidPushTargets());

            }
        }

        validTargets.sort(Comparator.comparingInt(
            c -> c.storage().getMaxEnergyStored() - c.storage().getEnergyStored()
        ));

        //TODO long support
        long transferred = distributeEnergyEvenlyBetween(getTotalEnergyStored(), validTargets);
        this.totalEnergyStored = getTotalEnergyStored() - transferred;
    }

    public void markDirty() {
        nodes().forEach(CapacitorBankNode::markDirty);
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    protected void onNodeRemoved(CapacitorBankNode node) {
        totalMaxEnergyStored = Mth.clamp(0, Long.MAX_VALUE, totalMaxEnergyStored - node.getMaxEnergyStored());
        positions.remove(node.getPos());
    }

    @Override
    protected void onNodeAdded(CapacitorBankNode node) {
        if (node.getBlockEntity() == null) {
            return;
        }
        totalMaxEnergyStored = Mth.clamp(0, Long.MAX_VALUE, totalMaxEnergyStored + node.getMaxEnergyStored());
        if (positions == null) { //This is called in init when it's not ready yet
            return;
        }
        positions.add(node.getPos());
        node.getBlockEntity().setRedstoneControl(redstoneControl);
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
        this.totalMaxEnergyStored += other.getTotalMaxEnergyStored();
        this.totalEnergyStored += other.totalEnergyStored;
        other.totalEnergyStored = 0;
        this.positions.clear();
        this.positions.addAll(nodes().stream().map(CapacitorBankNode::getPos).toList());
        other.markDirty();
    }

    @Override
    protected void onGraphSplit(Set<CapacitorBankNetwork> newGraphs) {
        long energy = this.getTotalEnergyStored();
        long capacity = this.getTotalMaxEnergyStored();
        for (CapacitorBankNetwork n : newGraphs) {
            n.uuid = UUID.randomUUID();
            n.positions = new ArrayList<>();
            n.positions.addAll(n.nodes().stream().map(CapacitorBankNode::getPos).toList());
            n.markDirty();

            Iterator<CapacitorBankNode> it = n.nodes().iterator();

            if (it.hasNext()) {
                CapacitorBankNode node = it.next();

                if (node.getBlockEntity().getLevel() instanceof ServerLevel level) {
                    CapacitorBankSavedData.onNetworkCreated(level, n);
                }
            }
            capacity += n.getTotalMaxEnergyStored();
            this.totalMaxEnergyStored -= n.getTotalMaxEnergyStored();
        }

        for (CapacitorBankNetwork n : newGraphs) {
            long toShare = energy * n.getTotalMaxEnergyStored() / capacity;
            n.totalEnergyStored = toShare;
            energy -= toShare;
        }

        this.positions.addAll(nodes().stream().map(CapacitorBankNode::getPos).toList());
        this.totalEnergyStored = energy;
    }

    public record SidedPos(BlockPos pos, @Nullable Direction side) {}
}
