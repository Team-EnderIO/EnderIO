package com.enderio.conduits.common.conduit;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitNetworkContext;
import com.enderio.api.conduit.Conduit;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.mojang.datafixers.util.Pair;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@EventBusSubscriber
public class ConduitSavedData extends SavedData {

    private final Map<Holder<Conduit<?, ?, ?>>, List<Graph<ConduitGraphContext>>> networks = new HashMap<>();

    // Used to find the NodeIdentifier(s) of a conduit when it is loaded
    private final Map<Holder<Conduit<?, ?, ?>>, Map<ChunkPos, Map<BlockPos, ConduitGraphObject<?, ?>>>> deserializedNodes = new HashMap<>();

    public static ConduitSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(
            ConduitSavedData::new, ConduitSavedData::new),
            "enderio_conduit_network");
    }

    private ConduitSavedData() {
    }

    // region Serialization

    private static final String KEY_GRAPHS = "Graphs";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_GRAPH_OBJECTS = "GraphObjects";
    private static final String KEY_GRAPH_CONNECTIONS = "GraphConnections";
    private static final String KEY_GRAPH_CONTEXT = "GraphContext";

    // Deserialization
    private ConduitSavedData(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        ListTag graphsTag = nbt.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
        for (Tag tag : graphsTag) {
            CompoundTag typedGraphTag = (CompoundTag) tag;
            ResourceKey<Conduit<?, ?, ?>> type = ResourceKey.create(EnderIORegistries.Keys.CONDUIT,
                ResourceLocation.parse(typedGraphTag.getString(KEY_TYPE)));

            var registry = lookupProvider.lookupOrThrow(EnderIORegistries.Keys.CONDUIT);

            Optional<Holder.Reference<Conduit<?, ?, ?>>> typeHolder = registry.get(type);

            if (typeHolder.isPresent()) {
                ListTag graphsForTypeTag = typedGraphTag.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
                deserializeGraphs(lookupProvider, typeHolder.get(), graphsForTypeTag);
            } else {
                EnderIO.LOGGER.warn("Skipping graph for missing conduit type: " + type.toString());
            }
        }
    }

    private void deserializeGraphs(HolderLookup.Provider lookupProvider, Holder<Conduit<?, ?, ?>> typeHolder, ListTag graphs) {
        for (Tag tag1 : graphs) {
            CompoundTag graphTag = (CompoundTag) tag1;

            ListTag graphObjectsTag = graphTag.getList(KEY_GRAPH_OBJECTS, Tag.TAG_COMPOUND);
            ListTag graphConnectionsTag = graphTag.getList(KEY_GRAPH_CONNECTIONS, Tag.TAG_COMPOUND);

            List<ConduitGraphObject<?, ?>> graphObjects = new ArrayList<>();
            List<Pair<? extends ConduitGraphObject<?, ?>, ? extends ConduitGraphObject<?, ?>>> connections = new ArrayList<>();

            for (int i = 0; i < graphObjectsTag.size(); i++) {
                CompoundTag nodeTag = graphObjectsTag.getCompound(i);
                var node = ConduitGraphObject.CODEC
                    .decode(lookupProvider.createSerializationContext(NbtOps.INSTANCE), nodeTag)
                    .getOrThrow().getFirst();

                graphObjects.add(node);
                putUnloadedNodeIdentifier(typeHolder, node.getPos(), node);
            }

            for (Tag tag2 : graphConnectionsTag) {
                CompoundTag connectionTag = (CompoundTag) tag2;
                connections.add(new Pair<>(graphObjects.get(connectionTag.getInt("0")), graphObjects.get(connectionTag.getInt("1"))));
            }

            ConduitGraphObject<?, ?> graphObject = graphObjects.get(0);
            if (graphTag.contains(KEY_GRAPH_CONTEXT)) {
                ConduitGraphUtility.integrateWithLoad(typeHolder, graphObject, List.of(), graphTag.getCompound(KEY_GRAPH_CONTEXT));
            } else {
                ConduitGraphUtility.integrate(typeHolder, graphObject, List.of());
            }

            merge(typeHolder, graphObject, connections);

            networks.computeIfAbsent(typeHolder, t -> new ArrayList<>()).add(graphObject.getGraph());
        }
    }

    // Serialization

    /* NBT layout
    data
      ┖ graphs (list)
          ┖ [index]
              ┠ type: [conduit type] (ex. "enderio:power3")
              ┖ graphs (list) // One type of conduits' graphs
                  ┠ [element]
                  ┃   ┠ graphConnections (list)
                  ┃   ┃   ┖ [element]
                  ┃   ┃       ┠ 0: [first object's index]
                  ┃   ┃       ┖ 1: [second object's index]
                  ┃   ┖ graphObjects (list)
                  ┃       ┖ [element]
                  ┃         ┠ pos:
                  ┃         ┃   ┠ x:
                  ┃         ┃   ┠ y:
                  ┃         ┃   ┖ z:
                  ┃         ┖ data: Compound based on type
                  ┖ [next element]
                      ┖ ...
     */

    // Serialization
    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        ListTag graphsTag = new ListTag();
        for (var entry : networks.entrySet()) {
            Holder<Conduit<?, ?, ?>> type = entry.getKey();
            List<Graph<ConduitGraphContext>> graphs = entry.getValue();
            if (graphs.isEmpty() || !type.isBound()) {
                continue;
            }

            CompoundTag typedGraphTag = new CompoundTag();
            typedGraphTag.putString(KEY_TYPE, type.getRegisteredName());

            ListTag graphsForTypeTag = new ListTag();

            for (Graph<ConduitGraphContext> graph : graphs) {
                if (!graph.getObjects().isEmpty()) {
                    graphsForTypeTag.add(serializeGraph(lookupProvider, graph));
                }
            }

            if (!graphsForTypeTag.isEmpty()) {
                typedGraphTag.put(KEY_GRAPHS, graphsForTypeTag);
                graphsTag.add(typedGraphTag);
            }
        }

        nbt.put(KEY_GRAPHS, graphsTag);
        return nbt;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    private static CompoundTag serializeGraph(HolderLookup.Provider lookupProvider, Graph<ConduitGraphContext> graph) {
        List<GraphObject<ConduitGraphContext>> graphObjects = new ArrayList<>(graph.getObjects());
        List<Pair<GraphObject<ConduitGraphContext>, GraphObject<ConduitGraphContext>>> connections = new ArrayList<>();

        CompoundTag graphTag = new CompoundTag();

        var context = graph.getContextData();
        if (context.canSerialize()) {
            graphTag.put(KEY_GRAPH_CONTEXT, context.save());
        }

        ListTag graphObjectsTag = new ListTag();
        ListTag graphConnectionsTag = new ListTag();

        for (GraphObject<ConduitGraphContext> graphObject : graphObjects) {
            for (GraphObject<ConduitGraphContext> neighbour : graph.getNeighbours(graphObject)) {
                var connection = new Pair<>(graphObject, neighbour);
                if (!containsConnection(connections, connection)) {
                    connections.add(connection);
                }
            }

            if (graphObject instanceof ConduitGraphObject<?, ?> conduitGraphObject) {
                var tag = ConduitGraphObject.CODEC
                    .encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), conduitGraphObject)
                    .getOrThrow();

                graphObjectsTag.add(tag);
            } else {
                throw new ClassCastException("graphObject was not of type nodeIdentifier");
            }
        }

        for (var connection : connections) {
            CompoundTag connectionTag = new CompoundTag();

            connectionTag.put("0", IntTag.valueOf(graphObjects.indexOf(connection.getFirst())));
            connectionTag.put("1", IntTag.valueOf(graphObjects.indexOf(connection.getSecond())));

            graphConnectionsTag.add(connectionTag);
        }

        graphTag.put(KEY_GRAPH_OBJECTS, graphObjectsTag);
        graphTag.put(KEY_GRAPH_CONNECTIONS, graphConnectionsTag);

        return graphTag;
    }

    // endregion

    private void merge(Holder<Conduit<?, ?, ?>> conduitType, GraphObject<ConduitGraphContext> object,
        List<Pair<? extends ConduitGraphObject<?, ?>, ? extends ConduitGraphObject<?, ?>>> connections) {
        var filteredConnections = connections.stream().filter(pair -> (pair.getFirst() == object || pair.getSecond() == object)).toList();

        List<? extends ConduitGraphObject<?, ?>> neighbors = filteredConnections
            .stream()
            .map(pair -> pair.getFirst() == object ? pair.getSecond() : pair.getFirst())
            .toList();

        for (var neighbor : neighbors) {
            ConduitGraphUtility.connect(conduitType, object, neighbor);
        }

        connections = connections.stream().filter(v -> !filteredConnections.contains(v)).toList();
        if (!connections.isEmpty()) {
            merge(conduitType, connections.get(0).getFirst(), connections);
        }
    }

    @Nullable
    public ConduitGraphObject<?, ?> takeUnloadedNodeIdentifier(Holder<Conduit<?, ?, ?>> type, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        Map<ChunkPos, Map<BlockPos, ConduitGraphObject<?, ?>>> typeMap = deserializedNodes.get(type);
        if (typeMap == null) {
            EnderIO.LOGGER.warn("Conduit data is missing!");
            return null;
        }
        Map<BlockPos, ConduitGraphObject<?, ?>> chunkMap = typeMap.get(chunkPos);
        if (chunkMap == null) {
            EnderIO.LOGGER.warn("Conduit data is missing!");
            return null;
        }
        ConduitGraphObject<?, ?> node = chunkMap.get(pos);

        chunkMap.remove(pos);
        if (chunkMap.isEmpty()) {
            typeMap.remove(chunkPos);
        }

        if (typeMap.isEmpty()) {
            deserializedNodes.remove(type);
        }

        return node;
    }

    public void putUnloadedNodeIdentifier(Holder<Conduit<?, ?, ?>> type, BlockPos pos, ConduitGraphObject<?, ?> node) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Map<ChunkPos, Map<BlockPos, ConduitGraphObject<?, ?>>> typeMap = deserializedNodes.computeIfAbsent(type, k -> new HashMap<>());
        Map<BlockPos, ConduitGraphObject<?, ?>> chunkMap = typeMap.computeIfAbsent(chunkPos, k -> new HashMap<>());
        chunkMap.put(pos, node);
    }

    private static <T extends Mergeable<T>> boolean containsConnection(List<Pair<GraphObject<T>, GraphObject<T>>> connections,
        Pair<GraphObject<T>, GraphObject<T>> connection) {
        return connections.contains(connection) || connections.contains(connection.swap());
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            get(serverLevel).tick(serverLevel);
        }
    }

    private void tick(ServerLevel serverLevel) {
        setDirty();
        for (var entry: networks.entrySet()) {
            entry.getValue().removeIf(graph -> graph.getObjects().isEmpty() || graph.getObjects().iterator().next().getGraph() != graph);
        }

        for (var entry : networks.entrySet()) {
            for (var graph : entry.getValue()) {
                var conduitType = entry.getKey();
                var conduitTicker = conduitType.value().getTicker();
                tickConduitGraph(serverLevel, entry.getKey(), conduitTicker, graph);
            }
        }
    }

    private <T extends Conduit<T, U, V>, U extends ConduitNetworkContext<U>, V extends ConduitData<V>> void tickConduitGraph(ServerLevel serverLevel,
        Holder<Conduit<?, ?, ?>> conduitType, ConduitTicker<T, U, V> ticker, Graph<ConduitGraphContext> graph) {
        if (serverLevel.getGameTime() % ticker.getTickRate() == 0) {
            //noinspection unchecked
            ticker.tickGraph(serverLevel, (T)conduitType.value(), new WrappedConduitNetwork<>(graph), ConduitSavedData::isRedstoneActive);
        }
    }

    private static boolean isRedstoneActive(ServerLevel serverLevel, BlockPos pos, ColorControl color) {
        if (!serverLevel.isLoaded(pos) || !serverLevel.shouldTickBlocksAt(pos)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(pos) instanceof ConduitBlockEntity conduit)) {
            return false;
        }

        var registry = serverLevel.holderLookup(EnderIORegistries.Keys.CONDUIT);
        var redstoneConduitType = registry.get(EIOConduitTypes.Types.REDSTONE);

        if (redstoneConduitType.isEmpty() || !conduit.getBundle().getTypes().contains(redstoneConduitType.get())) {
            return false;
        }

        var node = conduit.getBundle().getNodeFor(redstoneConduitType.get());
        RedstoneConduitData data = (RedstoneConduitData)node.getConduitData();
        return data.isActive(color);
    }

    public static <T extends ConduitNetworkContext<T>> void addPotentialGraph(Holder<Conduit<?, ?, ?>> type, Graph<ConduitGraphContext> graph, ServerLevel level) {
        get(level).addPotentialGraph(type, graph);
    }

    private <T extends ConduitNetworkContext<T>> void addPotentialGraph(Holder<Conduit<?, ?, ?>> type, Graph<ConduitGraphContext> graph) {
        if (!networks.computeIfAbsent(type, unused -> new ArrayList<>()).contains(graph)) {
            networks.get(type).add(graph);
        }
    }

    @Override
    public void save(File file, HolderLookup.Provider lookupProvider) {
        if (isDirty()) {
            //This is an exact copy of Mekanism MekanismSavedData's system which is loosely based on
            // Refined Storage's RSSavedData's system of saving first to a temp file
            // to reduce the odds of corruption if the user's computer crashes while the file is being written

            //Thanks pupnewfster
            File tempFile = file.toPath().getParent().resolve(file.getName() + ".tmp").toFile();
            super.save(tempFile, lookupProvider);
            if (file.exists() && !file.delete()) {
                EnderIO.LOGGER.error("Failed to delete " + file.getName());
            }
            if (!tempFile.renameTo(file)) {
                EnderIO.LOGGER.error("Failed to rename " + tempFile.getName());
            }
        }
    }
}
