package com.enderio.conduits.common.network;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.api.conduit.IExtendedConduitData;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.ConduitNBTKeys;
import com.enderio.conduits.EIOConduits;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.types.RedstoneExtendedData;
import com.mojang.datafixers.util.Pair;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@Mod.EventBusSubscriber
public class ConduitSavedData extends SavedData {

    private final Map<IConduitType<?>, List<Graph<Mergeable.Dummy>>> networks = new HashMap<>();

    // Used to find the NodeIdentifier(s) of a conduit when it is loaded
    private final Map<IConduitType<?>, Map<ChunkPos, Map<BlockPos, NodeIdentifier<?>>>> deserializedNodes = new HashMap<>();

    public static ConduitSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(nbt -> new ConduitSavedData(level, nbt), ConduitSavedData::new, "enderio_conduit_network");
    }

    private ConduitSavedData() {

    }

    // region Serialization

    private static final String KEY_GRAPHS = "Graphs";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_GRAPH_OBJECTS = "GraphObjects";
    private static final String KEY_GRAPH_CONNECTIONS = "GraphConnections";
    private static final String KEY_DATA = "Data";

    // Deserialization
    private ConduitSavedData(Level level, CompoundTag nbt) {
        ListTag graphsTag = nbt.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
        for (Tag tag : graphsTag) {
            CompoundTag typedGraphTag = (CompoundTag) tag;
            ResourceLocation type = new ResourceLocation(typedGraphTag.getString(KEY_TYPE));

            if (ConduitTypes.getRegistry().containsKey(type)) {
                IConduitType<?> value = Objects.requireNonNull(ConduitTypes.getRegistry().getValue(type));
                ListTag graphsForTypeTag = typedGraphTag.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
                for (Tag tag1 : graphsForTypeTag) {
                    CompoundTag graphTag = (CompoundTag) tag1;

                    ListTag graphObjectsTag = graphTag.getList(KEY_GRAPH_OBJECTS, Tag.TAG_COMPOUND);
                    ListTag graphConnectionsTag = graphTag.getList(KEY_GRAPH_CONNECTIONS, Tag.TAG_COMPOUND);

                    List<NodeIdentifier<?>> graphObjects = new ArrayList<>();
                    List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();

                    for (Tag tag2 : graphObjectsTag) {
                        CompoundTag nodeTag = (CompoundTag) tag2;
                        BlockPos pos = BlockPos.of((nodeTag.getLong(ConduitNBTKeys.BLOCK_POS)));
                        NodeIdentifier<?> node = new NodeIdentifier<>(pos, value.createExtendedConduitData(level, pos));
                        node.getExtendedConduitData().deserializeNBT(nodeTag.getCompound(KEY_DATA));
                        graphObjects.add(node);
                        putUnloadedNodeIdentifier(value, pos, node);
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
                  ┃         ┠ pos: long (representing BlockPos.asLong())
                  ┃         ┖ data: Compound based on type
                  ┖ [next element]
                      ┖ ...
     */

    // Serialization
    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag graphsTag = new ListTag();
        for (IConduitType<?> type : networks.keySet()) {
            List<Graph<Mergeable.Dummy>> graphs = networks.get(type);
            if (graphs.isEmpty())
                continue;

            CompoundTag typedGraphTag = new CompoundTag();
            typedGraphTag.putString(KEY_TYPE, ConduitTypes.getRegistry().getKey(type).toString());

            ListTag graphsForTypeTag = new ListTag();

            for (Graph<Mergeable.Dummy> graph : graphs) {
                if (graph.getObjects().isEmpty())
                    continue;

                graphsForTypeTag.add(serializeGraph(graph));
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

    private static CompoundTag serializeGraph(Graph<Mergeable.Dummy> graph) {
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
                CompoundTag dataTag = new CompoundTag();
                dataTag.putLong(ConduitNBTKeys.BLOCK_POS, nodeIdentifier.getPos().asLong());
                dataTag.put(KEY_DATA, nodeIdentifier.getExtendedConduitData().serializeNBT());
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
    public <T extends IExtendedConduitData<T>> NodeIdentifier<T> takeUnloadedNodeIdentifier(IConduitType<T> type, BlockPos pos) {
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
        if (chunkMap.size() == 0)
            typeMap.remove(chunkPos);
        if (typeMap.size() == 0)
            deserializedNodes.remove(type);

        return (NodeIdentifier<T>) node;
    }

    public void putUnloadedNodeIdentifier(IConduitType<?> type, BlockPos pos, NodeIdentifier<?> node) {
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
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        if (event.level instanceof ServerLevel serverLevel) {
            get(serverLevel).tick(serverLevel);
        }
    }

    private void tick(ServerLevel serverLevel) {
        setDirty();
        for (IConduitType<?> type : networks.keySet()) {
            List<Graph<Mergeable.Dummy>> graphs = networks.get(type);
            graphs.removeIf(graph -> graph.getObjects().isEmpty() || graph.getObjects().iterator().next().getGraph() != graph);
        }
        for (var entry : networks.entrySet()) {
            for (Graph<Mergeable.Dummy> graph : entry.getValue()) {
                if (serverLevel.getGameTime() % entry.getKey().getTicker().getTickRate() == ConduitTypes.getRegistry().getID(entry.getKey()) % entry
                    .getKey()
                    .getTicker()
                    .getTickRate()) {
                    entry.getKey().getTicker().tickGraph(entry.getKey(), graph, serverLevel, ConduitSavedData::isRedstoneActive);
                }
            }
        }
    }

    private static boolean isRedstoneActive(ServerLevel serverLevel, BlockPos pos, ColorControl color) {
        if (!serverLevel.isLoaded(pos) || !serverLevel.shouldTickBlocksAt(pos))
            return false;
        if (!(serverLevel.getBlockEntity(pos) instanceof ConduitBlockEntity conduit))
            return false;
        if (!conduit.getBundle().getTypes().contains(EnderConduitTypes.REDSTONE.get()))
            return false;
        RedstoneExtendedData data = conduit.getBundle().getNodeFor(EnderConduitTypes.REDSTONE.get()).getExtendedConduitData().cast();
        return data.isActive(color);
    }
    public static void addPotentialGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph, ServerLevel level) {
        get(level).addPotentialGraph(type, graph);
    }

    private void addPotentialGraph(IConduitType<?> type, Graph<Mergeable.Dummy> graph) {
        if (!networks.computeIfAbsent(type, unused -> new ArrayList<>()).contains(graph)) {
            networks.get(type).add(graph);
        }
    }

    @Override
    public void save(File file) {
        if (isDirty()) {
            //This is an exact copy of Mekanism MekanismSavedData's system which is loosely based on
            // Refined Storage's RSSavedData's system of saving first to a temp file
            // to reduce the odds of corruption if the user's computer crashes while the file is being written

            //Thanks pupnewster
            File tempFile = file.toPath().getParent().resolve(file.getName() + ".tmp").toFile();
            super.save(tempFile);
            if (file.exists() && !file.delete()) {
                EnderIO.LOGGER.error("Failed to delete " + file.getName());
            }
            if (!tempFile.renameTo(file)) {
                EnderIO.LOGGER.error("Failed to rename " + tempFile.getName());
            }
        }
    }
}
