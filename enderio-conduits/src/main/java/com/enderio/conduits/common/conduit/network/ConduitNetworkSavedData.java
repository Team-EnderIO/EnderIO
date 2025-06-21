package com.enderio.conduits.common.conduit.network;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.EnderIOConduitsRegistries;
import com.enderio.conduits.api.network.ConduitNetworkContext;
import com.enderio.conduits.api.network.ConduitNetworkContextType;
import com.enderio.conduits.api.ticker.ConduitTicker;
import com.enderio.core.common.graph.Network;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID)
public class ConduitNetworkSavedData extends SavedData {

    public static final Codec<ConduitNetworkSavedData> CODEC = ConduitNetwork.CODEC.listOf()
            .xmap(ConduitNetworkSavedData::new, ConduitNetworkSavedData::getNetworks);

    private final Multimap<Holder<Conduit<?, ?>>, ConduitNetwork> networks = HashMultimap.create();

    private final Multimap<Long, ConduitNetwork> networksByChunk = HashMultimap.create();
    private final Multimap<ConduitNetwork, Long> chunksByNetwork = HashMultimap.create();

    private final Map<Long, Boolean> tickingChunksMap = Maps.newHashMap();

    private final Map<Holder<Conduit<?, ?>>, Map<BlockPos, ConduitNode>> unloadedNodes = Maps.newHashMap();

    private static final String KEY_NEW_DATA = "Networks";

    public static ConduitNetworkSavedData get(ServerLevel level) {
        return level.getDataStorage()
                .computeIfAbsent(new Factory<>(ConduitNetworkSavedData::new, ConduitNetworkSavedData::load),
                        "enderio_conduit_network");
    }

    public ConduitNetworkSavedData() {
    }

    private ConduitNetworkSavedData(List<ConduitNetwork> networks) {
        for (ConduitNetwork network : networks) {
            network.setOnChunkCoverageChanged(this::onNetworkChunksChanged);
            this.networks.put(network.conduit(), network);

            for (var node : network.nodes()) {
                unloadedNodes.computeIfAbsent(network.conduit(), c -> Maps.newHashMap()).put(node.pos(), node);
            }
        }
    }

    private static ConduitNetworkSavedData load(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        // TODO: 1.22 - remove support for the legacy graph format.
        if (nbt.contains(KEY_GRAPHS)) {
            return loadLegacy(nbt, lookupProvider);
        }

        // TODO: Are we handling partials fine here?
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), nbt.get(KEY_NEW_DATA))
                .getPartialOrThrow();
    }

    private List<ConduitNetwork> getNetworks() {
        return networks.values().stream().filter(n -> n.isValid() && !n.isEmpty()).toList();
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        compoundTag.put(KEY_NEW_DATA,
                CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow());
        return compoundTag;
    }

    @Nullable
    public ConduitNode claimNode(Holder<Conduit<?, ?>> conduit, BlockPos pos) {
        var conduitMap = unloadedNodes.get(conduit);
        if (conduitMap == null) {
            LOGGER.warn("Conduit data is missing!");
            return null;
        }

        if (!conduitMap.containsKey(pos)) {
            LOGGER.warn("Conduit data is missing node at {}", pos);
            return null;
        }

        return conduitMap.remove(pos);
    }

    public void returnNode(Holder<Conduit<?, ?>> conduit, BlockPos pos, ConduitNode node) {
        unloadedNodes.computeIfAbsent(conduit, c -> Maps.newHashMap()).put(pos, node);
    }

    public static void onNetworkCreated(ServerLevel level, ConduitNetwork network) {
        get(level).onNetworkCreated(network);
    }

    private void onNetworkCreated(ConduitNetwork network) {
        Preconditions.checkArgument(network.isValid(), "New network is not valid!");
        networks.put(network.conduit(), network);
        onNetworkChunksChanged(network);
        network.setOnChunkCoverageChanged(this::onNetworkChunksChanged);
    }

    public static void onNetworkDiscarded(ServerLevel level, ConduitNetwork network) {
        Preconditions.checkArgument(network.isDiscarded(), "Network is not discarded!");
        get(level).onNetworkDiscarded(network);
    }

    private void onNetworkDiscarded(ConduitNetwork network) {
        // Allow empty or discarded networks here
        networks.remove(network.conduit(), network);

        for (var chunk : network.allChunks()) {
            networksByChunk.remove(chunk, network);
            chunksByNetwork.remove(network, chunk);
        }
    }

    private void onNetworkChunksChanged(ConduitNetwork network) {
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
        networks.values()
                .stream()
                .filter(n -> !n.isValid() || n.isEmpty())
                .toList() // avoid CME
                .forEach(this::onNetworkDiscarded);

        // Detect any chunk tick state changes
        for (var chunkPos : networksByChunk.keySet()) {
            boolean isTicking = serverLevel.shouldTickBlocksAt(chunkPos);
            if (!tickingChunksMap.containsKey(chunkPos) || isTicking != tickingChunksMap.get(chunkPos)) {
                tickingChunksMap.put(chunkPos, isTicking);
                networksByChunk.get(chunkPos).forEach(n -> n.onChunkTickStatusChanged(chunkPos));
            }
        }

        Registry<Conduit<?, ?>> conduitRegistry = serverLevel.registryAccess()
                .registryOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT);

        for (var conduit : networks.keySet()) {
            // Skip non-ticking graphs.
            var ticker = conduit.value().ticker();
            if (ticker == null) {
                continue;
            }

            int conduitId = conduitRegistry.getId(conduit.value());
            for (var network : networks.get(conduit)) {
                try {
                    tickNetwork(serverLevel, conduit, conduitId, ticker, network);
                } catch (Throwable t) {
                    var report = CrashReport.forThrowable(t, "Ticking conduit network");
                    var category = report.addCategory(conduit.getRegisteredName() + " network being ticked");
                    network.addCrashInfo(category);
                    throw new ReportedException(report);
                }
            }
        }
    }

    private <T extends Conduit<T, ?>> void tickNetwork(ServerLevel serverLevel, Holder<Conduit<?, ?>> conduit,
            int conduitId, ConduitTicker<T> ticker, ConduitNetwork network) {

        int conduitTickRate = conduit.value().networkTickRate();

        // TODO: Offsets for networks so they don't all tick on the same tick.
        if ((serverLevel.getGameTime()) % conduitTickRate == conduitId % conduitTickRate) {
            // Perform pre-tick network actions
            network.beforeTicking();

            // noinspection unchecked
            ticker.tick(serverLevel, (T) conduit.value(), network);
        }
    }

    @SubscribeEvent
    public static void onChunkUnloaded(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            get(serverLevel).tickingChunksMap.remove(event.getChunk().getPos().toLong());
        }
    }

    // region Legacy Serialization

    private static final Logger LOGGER = LogUtils.getLogger();

    // TODO: 1.22 - Remove

    private static final String KEY_GRAPHS = "Graphs";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_GRAPH_OBJECTS = "GraphObjects";
    private static final String KEY_GRAPH_CONNECTIONS = "GraphConnections";
    private static final String KEY_GRAPH_CONTEXT = "GraphContext";

    // TODO: Write it.

    private static ConduitNetworkSavedData loadLegacy(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        var savedData = new ConduitNetworkSavedData();

        ListTag graphsTag = nbt.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
        for (Tag tag : graphsTag) {
            CompoundTag typedGraphTag = (CompoundTag) tag;
            ResourceKey<Conduit<?, ?>> conduitKey = ResourceKey.create(EnderIOConduitsRegistries.Keys.CONDUIT,
                    ResourceLocation.parse(typedGraphTag.getString(KEY_TYPE)));

            var registry = lookupProvider.lookupOrThrow(EnderIOConduitsRegistries.Keys.CONDUIT);

            Optional<Holder.Reference<Conduit<?, ?>>> conduit = registry.get(conduitKey);

            if (conduit.isPresent()) {
                ListTag graphsForTypeTag = typedGraphTag.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
                deserializeGraphs(lookupProvider, conduit.get(), graphsForTypeTag, savedData);
            } else {
                LOGGER.warn("Skipping graph for missing conduit: {}", conduitKey);
            }
        }

        return savedData;
    }

    private static void deserializeGraphs(HolderLookup.Provider lookupProvider, Holder<Conduit<?, ?>> conduit,
            ListTag graphs, ConduitNetworkSavedData savedData) {
        for (Tag tag1 : graphs) {
            CompoundTag graphTag = (CompoundTag) tag1;

            ListTag graphObjectsTag = graphTag.getList(KEY_GRAPH_OBJECTS, Tag.TAG_COMPOUND);
            ListTag graphConnectionsTag = graphTag.getList(KEY_GRAPH_CONNECTIONS, Tag.TAG_COMPOUND);

            // Skip any graphs which have no objects in them, they should not have been
            // saved.
            if (graphObjectsTag.isEmpty()) {
                // TODO: Warning or something?
                continue;
            }

            // Load all nodes
            List<ConduitNode> nodes = new ArrayList<>();
            for (int i = 0; i < graphObjectsTag.size(); i++) {
                CompoundTag nodeTag = graphObjectsTag.getCompound(i);
                var node = ConduitNode.CODEC.decode(lookupProvider.createSerializationContext(NbtOps.INSTANCE), nodeTag)
                        .getOrThrow()
                        .getFirst();

                nodes.add(node);
            }

            // Load all connection indices
            List<Pair<Integer, Integer>> connections = new ArrayList<>();
            for (Tag tag2 : graphConnectionsTag) {
                CompoundTag connectionTag = (CompoundTag) tag2;
                connections.add(new Pair<>(connectionTag.getInt("0"), connectionTag.getInt("1")));
            }

            // Load network context
            ConduitNetworkContext<?> context = null;
            if (graphTag.contains(KEY_GRAPH_CONTEXT)) {
                context = loadNetworkContext(lookupProvider, graphTag.getCompound(KEY_GRAPH_CONTEXT));
            }

            // Create network
            var network = new ConduitNetwork(conduit, Optional.ofNullable(context), nodes,
                    new Network.IndexedEdgeList(connections));

            // Ensure the nodes are all valid
            if (nodes.stream().anyMatch(n -> !n.isValid())) {
                LOGGER.error(
                        "Node(s) are still invalid after loading the network. Please report this issue to Ender IO, loading cannot continue.");
                throw new IllegalStateException("Graph was null after loading the conduit network");
            }

            savedData.networks.put(conduit, network);

            for (var node : network.nodes()) {
                savedData.unloadedNodes.computeIfAbsent(network.conduit(), c -> Maps.newHashMap())
                        .put(node.pos(), node);
            }
        }
    }

    @Nullable
    private static ConduitNetworkContext<?> loadNetworkContext(HolderLookup.Provider lookupProvider,
            CompoundTag contextTag) {
        ResourceLocation serializerKey = ResourceLocation.parse(contextTag.getString("Type"));
        ConduitNetworkContextType<?> contextType = Objects.requireNonNull(
                EnderIOConduitsRegistries.CONDUIT_NETWORK_CONTEXT_TYPE.get(serializerKey),
                "Unable to find conduit network context type with key " + serializerKey);

        if (contextType.codec() == null) {
            return null;
        }

        // TODO: We're using getOrThrow a lot for conduits. Should definitely make more
        // robust/flexible.
        CompoundTag data = contextTag.getCompound("Data");
        var ops = lookupProvider.createSerializationContext(NbtOps.INSTANCE);
        return contextType.codec().decode(ops, ops.getMap(data).getOrThrow()).getOrThrow();
    }

    // endregion
}
