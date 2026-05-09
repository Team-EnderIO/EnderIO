package com.enderio.enderio.content.conduits.network;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.conduits.Conduit;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import com.enderio.enderio.api.conduits.ConduitType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class ConduitNetworkSavedData extends SavedData {

    public static final Codec<ConduitNetworkSavedData> CODEC = ConduitNetworkImpl.CODEC
        .listOf()
        .xmap(ConduitNetworkSavedData::new, ConduitNetworkSavedData::getNetworks);

    public static final SavedDataType<ConduitNetworkSavedData> TYPE = new SavedDataType<>(EnderIO.id("conduit_network"), ConduitNetworkSavedData::new, CODEC);

    private static final Logger LOGGER = LogUtils.getLogger();

    private final Multimap<ConduitType<?, ?>, ConduitNetworkImpl> networks = HashMultimap.create();

    private final Multimap<Long, ConduitNetworkImpl> networksByChunk = HashMultimap.create();
    private final Multimap<ConduitNetworkImpl, Long> chunksByNetwork = HashMultimap.create();

    private final Map<Long, Boolean> tickingChunksMap = Maps.newHashMap();

    private final Map<ConduitType<?, ?>, Map<BlockPos, ConduitNodeImpl>> unloadedNodes = Maps.newHashMap();

    public static ConduitNetworkSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    private ConduitNetworkSavedData() {
    }

    private ConduitNetworkSavedData(List<ConduitNetworkImpl> networks) {
        for (ConduitNetworkImpl network : networks) {
            network.setOnChunkCoverageChanged(this::onNetworkChunksChanged);
            this.networks.put(network.conduitType(), network);

            for (var node : network.nodes()) {
                unloadedNodes.computeIfAbsent(network.conduitType(), c -> Maps.newHashMap()).put(node.pos(), node);
            }
        }
    }

    private List<ConduitNetworkImpl> getNetworks() {
        return networks.values().stream().filter(n -> n.isValid() && !n.isEmpty()).toList();
    }

    @Nullable
    public ConduitNodeImpl claimNode(Holder<Conduit<?, ?>> conduit, BlockPos pos) {
        var conduitMap = unloadedNodes.get(conduit.value().type());
        if (conduitMap == null) {
            LOGGER.warn("Conduit data is missing!");
            return null;
        }

        if (!conduitMap.containsKey(pos)) {
            LOGGER.warn("Conduit data is missing node at {}", pos);
            return null;
        }

        var node = conduitMap.remove(pos);
        if (node.conduit() != conduit) {
            // Trust block entity as source of truth *just* because it's easier to rebuild a node than it is to reconcile the block entity's data.
            LOGGER.warn("Conduit mismatch at {}: {} (network) vs {} (block entity). Dumping data, node will be rebuilt.", pos, node.conduit(), conduit);
            return null;
        }

        return node;
    }

    public void returnNode(Holder<Conduit<?, ?>> conduit, BlockPos pos, ConduitNodeImpl node) {
        unloadedNodes.computeIfAbsent(conduit.value().type(), c -> Maps.newHashMap()).put(pos, node);
    }

    public static void onNetworkCreated(ServerLevel level, ConduitNetworkImpl network) {
        get(level).onNetworkCreated(network);
    }

    private void onNetworkCreated(ConduitNetworkImpl network) {
        Preconditions.checkArgument(network.isValid(), "New network is not valid!");
        networks.put(network.conduitType(), network);
        onNetworkChunksChanged(network);
        network.setOnChunkCoverageChanged(this::onNetworkChunksChanged);
    }

    public static void onNetworkDiscarded(ServerLevel level, ConduitNetworkImpl network) {
        Preconditions.checkArgument(network.isDiscarded(), "Network is not discarded!");
        get(level).onNetworkDiscarded(network);
    }

    private void onNetworkDiscarded(ConduitNetworkImpl network) {
        // Allow empty or discarded networks here
        networks.remove(network.conduitType(), network);

        for (var chunk : network.allChunks()) {
            networksByChunk.remove(chunk, network);
            chunksByNetwork.remove(network, chunk);
        }
    }

    private void onNetworkChunksChanged(ConduitNetworkImpl network) {
        var knownChunks = chunksByNetwork.get(network);
        var currentChunks = network.allChunks();

        // Find removed chunks
        var removedChunks = knownChunks.stream().filter(chunk -> !currentChunks.contains(chunk)).toList();

        // Find added chunks
        var addedChunks = currentChunks.stream().filter(chunk -> !knownChunks.contains(chunk)).toList();

        // Update collections
        for (var chunk : removedChunks) {
            networksByChunk.remove(chunk, network);
        }

        for (var chunk : addedChunks) {
            networksByChunk.put(chunk, network);
        }

        chunksByNetwork.get(network).clear();
        chunksByNetwork.get(network).addAll(currentChunks);
    }

    @Override
    public boolean isDirty() {
        // Always save networks when the opportunity arises
        return true;
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            get(serverLevel).tick(serverLevel);
        }
    }

    private void tick(ServerLevel serverLevel) {
        // Only remove empty graphs here
        networks.values().stream().filter(n -> !n.isValid() || n.isEmpty()).toList() // avoid CME
            .forEach(this::onNetworkDiscarded);

        // Detect any chunk load or tick state changes
        for (var chunkPos : networksByChunk.keySet()) {
            // Checking #hasChunk to resolve GH-1177. Unsure why hasChunk changes without firing a chunk unload event.
            boolean isTicking = serverLevel.hasChunk(ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos)) && serverLevel.shouldTickBlocksAt(chunkPos);
            if (!tickingChunksMap.containsKey(chunkPos) || isTicking != tickingChunksMap.get(chunkPos)) {
                tickingChunksMap.put(chunkPos, isTicking);
                networksByChunk.get(chunkPos).forEach(n -> n.onChunkTickStatusChanged(chunkPos));
            }
        }

        for (var conduitType : networks.keySet()) {
            // Ensure caches are ready for any networks that require them
            if (conduitType.doesRequireNetworkCaches()) {
                for (var network : networks.get(conduitType)) {
                    network.ensureCachesReady();
                }
            }

            // Skip non-ticking graphs.
            var ticker = conduitType.ticker();
            if (ticker == null) {
                continue;
            }

            // TODO: GH-1269
            int conduitId = EnderIORegistries.CONDUIT_TYPE.getId(conduitType);
            for (var network : networks.get(conduitType)) {
                try {
                    ticker.tick(serverLevel, network, conduitId);
                } catch (Throwable t) {
                    var report = CrashReport.forThrowable(t, "Ticking conduit network");
                    var category = report.addCategory(EnderIORegistries.CONDUIT_TYPE.getKey(conduitType) + " network being ticked");
                    network.addCrashInfo(category);
                    throw new ReportedException(report);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnloaded(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            get(serverLevel).tickingChunksMap.remove(event.getChunk().getPos().pack());
        }
    }
}
