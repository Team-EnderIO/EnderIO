package com.enderio.conduits.common.network;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ExtendedConduitData;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.misc.ColorControl;
import com.enderio.api.registry.EnderIORegistries;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.init.EIOConduitTypes;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.mojang.datafixers.util.Pair;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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
import java.util.Objects;

@EventBusSubscriber
public class ConduitSavedData extends SavedData {

    private final Map<ConduitType<?>, List<Graph<Mergeable.Dummy>>> networks = new HashMap<>();

    // Used to find the NodeIdentifier(s) of a conduit when it is loaded
    private final Map<ConduitType<?>, Map<ChunkPos, Map<BlockPos, NodeIdentifier<?>>>> deserializedNodes = new HashMap<>();

    public static ConduitSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(new Factory<>(
            ConduitSavedData::new,
            (nbt, lookupProvider) -> new ConduitSavedData(level, nbt, lookupProvider)),
            "enderio_conduit_network");
    }

    private ConduitSavedData() {
    }

    // region Serialization

    private static final String KEY_GRAPHS = "Graphs";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_GRAPH_OBJECTS = "GraphObjects";
    private static final String KEY_GRAPH_CONNECTIONS = "GraphConnections";

    // Deserialization
    private ConduitSavedData(Level level, CompoundTag nbt, HolderLookup.Provider lookupProvider) {
        ListTag graphsTag = nbt.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
        for (Tag tag : graphsTag) {
            CompoundTag typedGraphTag = (CompoundTag) tag;
            ResourceLocation type = new ResourceLocation(typedGraphTag.getString(KEY_TYPE));

            if (EnderIORegistries.CONDUIT_TYPES.containsKey(type)) {
                ConduitType<?> value = Objects.requireNonNull(EnderIORegistries.CONDUIT_TYPES.get(type));
                ListTag graphsForTypeTag = typedGraphTag.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
                for (Tag tag1 : graphsForTypeTag) {
                    CompoundTag graphTag = (CompoundTag) tag1;

                    ListTag graphObjectsTag = graphTag.getList(KEY_GRAPH_OBJECTS, Tag.TAG_COMPOUND);
                    ListTag graphConnectionsTag = graphTag.getList(KEY_GRAPH_CONNECTIONS, Tag.TAG_COMPOUND);

                    List<NodeIdentifier<?>> graphObjects = new ArrayList<>();
                    List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();

                    for (Tag tag2 : graphObjectsTag) {
                        CompoundTag nodeTag = (CompoundTag) tag2;
                        var node = NodeIdentifier.CODEC.decode(lookupProvider.createSerializationContext(NbtOps.INSTANCE), nodeTag)
                            .getOrThrow().getFirst();

                        graphObjects.add(node);
                        putUnloadedNodeIdentifier(value, node.getPos(), node);
                    }

                    for (Tag tag2 : graphConnectionsTag) {
                        CompoundTag connectionTag = (CompoundTag) tag2;
                        connections.add(new Pair<>(graphObjects.get(connectionTag.getInt("0")), graphObjects.get(connectionTag.getInt("1"))));
                    }

                    NodeIdentifier<?> graphObject = graphObjects.get(0);
                    Graph.integrate(graphObject, List.of());
                    merge(graphObject, connections);

                    networks.computeIfAbsent(value, ignored -> new ArrayList<>()).add(graphObject.getGraph());
                }
            }
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
            ConduitType<?> type = entry.getKey();
            List<Graph<Mergeable.Dummy>> graphs = entry.getValue();
            if (graphs.isEmpty()) {
                continue;
            }

            CompoundTag typedGraphTag = new CompoundTag();
            typedGraphTag.putString(KEY_TYPE, EnderIORegistries.CONDUIT_TYPES.getKey(type).toString());

            ListTag graphsForTypeTag = new ListTag();

            for (Graph<Mergeable.Dummy> graph : graphs) {
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

    private static CompoundTag serializeGraph(HolderLookup.Provider lookupProvider, Graph<Mergeable.Dummy> graph) {
        List<GraphObject<Mergeable.Dummy>> graphObjects = new ArrayList<>(graph.getObjects());
        List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();

        CompoundTag graphTag = new CompoundTag();

        ListTag graphObjectsTag = new ListTag();
        ListTag graphConnectionsTag = new ListTag();

        for (GraphObject<Mergeable.Dummy> graphObject : graphObjects) {
            for (GraphObject<Mergeable.Dummy> neighbour : graph.getNeighbours(graphObject)) {
                Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection = new Pair<>(graphObject, neighbour);
                if (!containsConnection(connections, connection)) {
                    connections.add(connection);
                }
            }

            if (graphObject instanceof NodeIdentifier<?> nodeIdentifier) {
                var tag = NodeIdentifier.CODEC
                    .encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), nodeIdentifier)
                    .getOrThrow();

                graphObjectsTag.add(tag);
            } else {
                throw new ClassCastException("graphObject was not of type nodeIdentifier");
            }
        }

        for (Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection : connections) {
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

    private void merge(GraphObject<Mergeable.Dummy> object, List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections) {
        var filteredConnections = connections.stream().filter(pair -> (pair.getFirst() == object || pair.getSecond() == object)).toList();
        List<GraphObject<Mergeable.Dummy>> neighbors = filteredConnections
            .stream()
            .map(pair -> pair.getFirst() == object ? pair.getSecond() : pair.getFirst())
            .toList();

        for (GraphObject<Mergeable.Dummy> neighbor : neighbors) {
            Graph.connect(object, neighbor);
        }

        connections = connections.stream().filter(v -> !filteredConnections.contains(v)).toList();
        if (!connections.isEmpty()) {
            merge(connections.get(0).getFirst(), connections);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends ExtendedConduitData<T>> NodeIdentifier<T> takeUnloadedNodeIdentifier(ConduitType<T> type, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        Map<ChunkPos, Map<BlockPos, NodeIdentifier<?>>> typeMap = deserializedNodes.get(type);
        if (typeMap == null) {
            EnderIO.LOGGER.warn("Conduit data is missing!");
            return null;
        }
        Map<BlockPos, NodeIdentifier<?>> chunkMap = typeMap.get(chunkPos);
        if (chunkMap == null) {
            EnderIO.LOGGER.warn("Conduit data is missing!");
            return null;
        }
        NodeIdentifier<?> node = chunkMap.get(pos);

        chunkMap.remove(pos);
        if (chunkMap.isEmpty()) {
            typeMap.remove(chunkPos);
        }

        if (typeMap.isEmpty()) {
            deserializedNodes.remove(type);
        }

        return (NodeIdentifier<T>) node;
    }

    public void putUnloadedNodeIdentifier(ConduitType<?> type, BlockPos pos, NodeIdentifier<?> node) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Map<ChunkPos, Map<BlockPos, NodeIdentifier<?>>> typeMap = deserializedNodes.computeIfAbsent(type, k -> new HashMap<>());
        Map<BlockPos, NodeIdentifier<?>> chunkMap = typeMap.computeIfAbsent(chunkPos, k -> new HashMap<>());
        chunkMap.put(pos, node);
    }

    private static boolean containsConnection(List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections,
        Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection) {
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
            for (Graph<Mergeable.Dummy> graph : entry.getValue()) {
                if (serverLevel.getGameTime() % entry.getKey().getTicker().getTickRate() == EnderIORegistries.CONDUIT_TYPES.getId(entry.getKey()) % entry
                    .getKey()
                    .getTicker()
                    .getTickRate()) {
                    entry.getKey().getTicker().tickGraph(entry.getKey(), graph, serverLevel, ConduitSavedData::isRedstoneActive);
                }
            }
        }
    }

    private static boolean isRedstoneActive(ServerLevel serverLevel, BlockPos pos, ColorControl color) {
        if (!serverLevel.isLoaded(pos) || !serverLevel.shouldTickBlocksAt(pos)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(pos) instanceof ConduitBlockEntity conduit)) {
            return false;
        }

        if (!conduit.getBundle().getTypes().contains(EIOConduitTypes.Types.REDSTONE.get())) {
            return false;
        }

        RedstoneExtendedData data = conduit.getBundle().getNodeFor(EIOConduitTypes.Types.REDSTONE.get()).getExtendedConduitData().cast();
        return data.isActive(color);
    }

    public static void addPotentialGraph(ConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level) {
        get(level).addPotentialGraph(type, graph);
    }

    private void addPotentialGraph(ConduitType<?> type, Graph<Mergeable.Dummy> graph) {
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
