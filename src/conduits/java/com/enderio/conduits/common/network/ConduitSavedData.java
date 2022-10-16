package com.enderio.conduits.common.network;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.EnderIO;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.datafixers.util.Pair;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class ConduitSavedData extends SavedData {

    private final ListMultimap<IConduitType<?>, Graph<Mergeable.Dummy>> networks = ArrayListMultimap.create();

    // Used to find the NodeIdentifier(s) of a conduit when it is loaded
    private final Map<IConduitType<?>, Map<ChunkPos, Map<BlockPos, NodeIdentifier<?>>>> deserializedNodes = new HashMap<>();

    public static ConduitSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(nbt -> new ConduitSavedData(level, nbt), ConduitSavedData::new, "enderio_conduit_network");
    }

    private ConduitSavedData() {

    }

    // Deserialization
    private ConduitSavedData(Level level, CompoundTag nbt) {
        EnderIO.LOGGER.info("Conduit network deserialization started");
        long start = System.currentTimeMillis();
        ListTag graphsTag = nbt.getList("graphs", Tag.TAG_COMPOUND);
        for (Tag tag : graphsTag) {
            CompoundTag typedGraphTag = (CompoundTag) tag;
            ResourceLocation type = new ResourceLocation(typedGraphTag.getString("type"));

            if (ConduitTypes.getRegistry().containsKey(type)) {
                IConduitType<?> value = Objects.requireNonNull(ConduitTypes.getRegistry().getValue(type));
                ListTag graphsForTypeTag = typedGraphTag.getList("graphs", Tag.TAG_COMPOUND);
                for (Tag tag1 : graphsForTypeTag) {
                    CompoundTag graphTag = (CompoundTag) tag1;

                    ListTag graphObjectsTag = graphTag.getList("graphObjects", Tag.TAG_COMPOUND);
                    ListTag graphConnectionsTag = graphTag.getList("graphConnections", Tag.TAG_COMPOUND);

                    List<NodeIdentifier<?>> graphObjects = new ArrayList<>();
                    List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();


                    for (Tag tag2 : graphObjectsTag) {
                        CompoundTag nodeTag = (CompoundTag) tag2;
                        BlockPos pos = BlockPos.of((nodeTag.getLong("pos")));
                        NodeIdentifier<?> node = new NodeIdentifier<>(pos, value.createExtendedConduitData(level, pos));
                        node.getExtendedConduitData().deserializeNBT(nodeTag.getCompound("data"));
                        graphObjects.add(node);
                        putUnloadedNodeIdentifier(value, pos, node);
                    }

                    for (Tag tag2: graphConnectionsTag) {
                        CompoundTag connectionTag = (CompoundTag) tag2;
                        connections.add(new Pair<>(graphObjects.get(connectionTag.getInt("0")), graphObjects.get(connectionTag.getInt("1"))));
                    }

                    NodeIdentifier<?> graphObject = graphObjects.get(0);
                    Graph.integrate(graphObject, List.of());
                    merge(graphObject, connections);

                    networks.get(value).add(graphObject.getGraph());
                }
            }
        }
        EnderIO.LOGGER.info("Conduit network deserialization finished, took {}ms", System.currentTimeMillis() - start);
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
                  ┃         ┠ pos: long (representing BlockPos.asLong())
                  ┃         ┖ data: Compound based on type
                  ┖ [next element]
                      ┖ ...
     */

    // Serialization
    @Override
    public CompoundTag save(CompoundTag nbt) {
        EnderIO.LOGGER.info("Conduit network serialization started");
        long start = System.currentTimeMillis();
        ListTag graphsTag = new ListTag();
        for (IConduitType<?> type: networks.keySet()) {
            List<Graph<Mergeable.Dummy>> graphs = networks.get(type);
            if (graphs.isEmpty())
                continue;

            CompoundTag typedGraphTag = new CompoundTag();
            typedGraphTag.putString("type", ConduitTypes.getRegistry().getKey(type).toString());

            ListTag graphsForTypeTag = new ListTag();

            for (Graph<Mergeable.Dummy> graph: graphs) {
                if (graph.getObjects().isEmpty())
                    continue;

                graphsForTypeTag.add(serializeGraph(graph));
            }
            if (!graphsForTypeTag.isEmpty()) {
                typedGraphTag.put("graphs", graphsForTypeTag);
                graphsTag.add(typedGraphTag);
            }
        }

        nbt.put("graphs", graphsTag);
        EnderIO.LOGGER.info("Conduit network serialization finished, took {}ms", System.currentTimeMillis() - start);
        return nbt;
    }

    private static CompoundTag serializeGraph(Graph<Mergeable.Dummy> graph) {
        List<GraphObject<Mergeable.Dummy>> graphObjects = new ArrayList<>(graph.getObjects());
        List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();

        CompoundTag graphTag = new CompoundTag();

        ListTag graphObjectsTag = new ListTag();
        ListTag graphConnectionsTag = new ListTag();

        for (GraphObject<Mergeable.Dummy> graphObject: graphObjects) {
            for (GraphObject<Mergeable.Dummy> neighbour : graph.getNeighbours(graphObject)) {
                Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection = new Pair<>(graphObject, neighbour);
                if (!containsConnection(connections, connection)) {
                    connections.add(connection);
                }
            }

            if (graphObject instanceof NodeIdentifier<?> nodeIdentifier) {
                CompoundTag dataTag = new CompoundTag();
                dataTag.putLong("pos", nodeIdentifier.getPos().asLong());
                dataTag.put("data", nodeIdentifier.getExtendedConduitData().serializeNBT());
                graphObjectsTag.add(dataTag);
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

        graphTag.put("graphObjects", graphObjectsTag);
        graphTag.put("graphConnections", graphConnectionsTag);

        return graphTag;
    }

    private void merge(GraphObject<Mergeable.Dummy> object, List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections) {
        var filteredConnections = connections.stream().filter(pair -> (pair.getFirst() == object || pair.getSecond() == object)).toList();
        List<GraphObject<Mergeable.Dummy>> neighbors = filteredConnections.stream().map(pair -> pair.getFirst() == object ? pair.getSecond() : pair.getFirst()).toList();


        for (GraphObject<Mergeable.Dummy> neighbor : neighbors) {
            Graph.connect(object, neighbor);
        }

        connections = connections.stream().filter(v -> !filteredConnections.contains(v)).toList();
        if (!connections.isEmpty()) {
            merge(connections.get(0).getFirst(), connections);
        }
    }

    public <T extends IExtendedConduitData<T>> NodeIdentifier<T> takeUnloadedNodeIdentifier(IConduitType<T> type, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        Map<ChunkPos, Map<BlockPos, NodeIdentifier<?>>> typeMap = Objects.requireNonNull(deserializedNodes.get(type), "Conduit data is missing!");
        Map<BlockPos, NodeIdentifier<?>> chunkMap = Objects.requireNonNull(typeMap.get(chunkPos), "Conduit data is missing!");
        NodeIdentifier<?> node = Objects.requireNonNull(chunkMap.get(pos), "Conduit data is missing!");

        chunkMap.remove(pos);
        if (chunkMap.size() == 0)
            typeMap.remove(chunkPos);
        if (typeMap.size() == 0)
            deserializedNodes.remove(type);

        return (NodeIdentifier<T>)node;
    }

    public void putUnloadedNodeIdentifier(IConduitType<?> type, BlockPos pos, NodeIdentifier<?> node) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Map<ChunkPos, Map<BlockPos, NodeIdentifier<?>>> typeMap = deserializedNodes.computeIfAbsent(type, k -> new HashMap<>());
        Map<BlockPos, NodeIdentifier<?>> chunkMap = typeMap.computeIfAbsent(chunkPos, k -> new HashMap<>());
        chunkMap.put(pos, node);
    }

    private static boolean containsConnection(List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections, Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection) {
        return connections.contains(connection) || connections.contains(connection.swap());
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        if (event.level instanceof ServerLevel serverLevel) {
            get(serverLevel).tick(serverLevel);
        }
    }

    private void tick(ServerLevel serverLevel) {
        setDirty();
        for (IConduitType<?> type: networks.keySet()) {
            List<Graph<Mergeable.Dummy>> graphs = networks.get(type);
            List<Graph<Mergeable.Dummy>> toRemove = new ArrayList<>();
            for (Graph<Mergeable.Dummy> graph : graphs) {
                if (graph.getObjects().isEmpty() || graph.getObjects().iterator().next().getGraph() != graph)
                    toRemove.add(graph);
            }
            graphs.removeAll(toRemove);
        }
        for (var entry : networks.entries()) {
            if (serverLevel.getGameTime() % entry.getKey().getTicker().getTickRate() == ConduitTypes.getRegistry().getID(entry.getKey()) % entry.getKey().getTicker().getTickRate()) {
                entry.getKey().getTicker().tickGraph(entry.getKey(), entry.getValue(), serverLevel);
            }
        }
    }

    public static void addPotentialGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level) {
        get(level).addPotentialGraph(type, graph);
    }

    private void addPotentialGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph) {
        if (!networks.get(type).contains(graph)) {
            networks.get(type).add(graph);
        }
    }
}
