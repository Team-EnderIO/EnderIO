package com.enderio.conduits.common.network;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ticker.ConduitTicker;
import com.enderio.conduits.common.conduit.ConduitGraphObject;
import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.ConduitNBTKeys;
import com.enderio.conduits.common.conduit.WrappedConduitGraph;
import com.enderio.conduits.common.conduit.block.ConduitBlockEntity;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
import com.enderio.conduits.common.init.EIOConduitTypes;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber
public class ConduitSavedData extends SavedData {

    private final Map<ConduitType<?>, List<Graph<Mergeable.Dummy>>> networks = new HashMap<>();

    // Used to find the NodeIdentifier(s) of a conduit when it is loaded
    private final Map<ConduitType<?>, Map<ChunkPos, Map<BlockPos, ConduitGraphObject<?>>>> deserializedNodes = new HashMap<>();

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

            if (EIOConduitTypes.REGISTRY.get().containsKey(type)) {
                ConduitType<?> value = Objects.requireNonNull(EIOConduitTypes.REGISTRY.get().getValue(type));
                ListTag graphsForTypeTag = typedGraphTag.getList(KEY_GRAPHS, Tag.TAG_COMPOUND);
                for (Tag tag1 : graphsForTypeTag) {
                    CompoundTag graphTag = (CompoundTag) tag1;

                    ListTag graphObjectsTag = graphTag.getList(KEY_GRAPH_OBJECTS, Tag.TAG_COMPOUND);
                    ListTag graphConnectionsTag = graphTag.getList(KEY_GRAPH_CONNECTIONS, Tag.TAG_COMPOUND);

                    List<ConduitGraphObject<?>> graphObjects = new ArrayList<>();
                    List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();

                    for (Tag tag2 : graphObjectsTag) {
                        CompoundTag nodeTag = (CompoundTag) tag2;
                        BlockPos pos = BlockPos.of((nodeTag.getLong(ConduitNBTKeys.BLOCK_POS)));
                        ConduitGraphObject<?> node = new ConduitGraphObject<>(pos, value.createConduitData(level, pos));
                        node.getConduitData().deserializeNBT(nodeTag.getCompound(KEY_DATA));
                        graphObjects.add(node);
                        putUnloadedNodeIdentifier(value, pos, node);
                    }

                    for (Tag tag2 : graphConnectionsTag) {
                        CompoundTag connectionTag = (CompoundTag) tag2;
                        connections.add(new Pair<>(graphObjects.get(connectionTag.getInt("0")), graphObjects.get(connectionTag.getInt("1"))));
                    }

                    ConduitGraphObject<?> graphObject = graphObjects.get(0);
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
        for (ConduitType<?> type : networks.keySet()) {
            List<Graph<Mergeable.Dummy>> graphs = networks.get(type);
            if (graphs.isEmpty()) {
                continue;
            }

            CompoundTag typedGraphTag = new CompoundTag();
            typedGraphTag.putString(KEY_TYPE, EIOConduitTypes.REGISTRY.get().getKey(type).toString());

            ListTag graphsForTypeTag = new ListTag();

            for (Graph<Mergeable.Dummy> graph : graphs) {
                if (!graph.getObjects().isEmpty()) {
                    graphsForTypeTag.add(serializeGraph(graph));
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

            if (graphObject instanceof ConduitGraphObject<?> conduitGraphObject) {
                CompoundTag dataTag = new CompoundTag();
                dataTag.putLong(ConduitNBTKeys.BLOCK_POS, conduitGraphObject.getPos().asLong());
                dataTag.put(KEY_DATA, conduitGraphObject.getConduitData().serializeNBT());
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
    public <T extends ConduitData<T>> ConduitGraphObject<T> takeUnloadedNodeIdentifier(ConduitType<T> type, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);

        Map<ChunkPos, Map<BlockPos, ConduitGraphObject<?>>> typeMap = deserializedNodes.get(type);
        if (typeMap == null) {
            EnderIO.LOGGER.warn("Conduit data is missing!");
            return null;
        }
        Map<BlockPos, ConduitGraphObject<?>> chunkMap = typeMap.get(chunkPos);
        if (chunkMap == null) {
            EnderIO.LOGGER.warn("Conduit data is missing!");
            return null;
        }
        ConduitGraphObject<?> node = chunkMap.get(pos);

        chunkMap.remove(pos);
        if (chunkMap.isEmpty()) {
            typeMap.remove(chunkPos);
        }

        if (typeMap.isEmpty()) {
            deserializedNodes.remove(type);
        }

        return (ConduitGraphObject<T>) node;
    }

    public void putUnloadedNodeIdentifier(ConduitType<?> type, BlockPos pos, ConduitGraphObject<?> node) {
        ChunkPos chunkPos = new ChunkPos(pos);
        Map<ChunkPos, Map<BlockPos, ConduitGraphObject<?>>> typeMap = deserializedNodes.computeIfAbsent(type, k -> new HashMap<>());
        Map<BlockPos, ConduitGraphObject<?>> chunkMap = typeMap.computeIfAbsent(chunkPos, k -> new HashMap<>());
        chunkMap.put(pos, node);
    }

    private static boolean containsConnection(List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections,
        Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection) {
        return connections.contains(connection) || connections.contains(connection.swap());
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }

        if (event.level instanceof ServerLevel serverLevel) {
            get(serverLevel).tick(serverLevel);
        }
    }

    private void tick(ServerLevel serverLevel) {
        setDirty();
        for (ConduitType<?> type : networks.keySet()) {
            List<Graph<Mergeable.Dummy>> graphs = networks.get(type);
            graphs.removeIf(graph -> graph.getObjects().isEmpty() || graph.getObjects().iterator().next().getGraph() != graph);
        }

        for (var entry : networks.entrySet()) {
            for (Graph<Mergeable.Dummy> graph : entry.getValue()) {
                tickConduitGraph(serverLevel, entry.getKey(), graph);
            }
        }
    }

    private <T extends ConduitData<T>> void tickConduitGraph(ServerLevel serverLevel, ConduitType<T> type, Graph<Mergeable.Dummy> graph) {
        ConduitTicker<T> conduitTicker = type.getTicker();

        if (serverLevel.getGameTime() % conduitTicker.getTickRate() == EIOConduitTypes.getConduitId(type) % conduitTicker.getTickRate()) {
            conduitTicker.tickGraph(serverLevel, type, new WrappedConduitGraph<>(graph), ConduitSavedData::isRedstoneActive);
        }
    }

    private static boolean isRedstoneActive(ServerLevel serverLevel, BlockPos pos, ColorControl color) {
        if (!serverLevel.isLoaded(pos) || !serverLevel.shouldTickBlocksAt(pos)) {
            return false;
        }

        if (!(serverLevel.getBlockEntity(pos) instanceof ConduitBlockEntity conduit)) {
            return false;
        }

        if (!conduit.getBundle().getTypes().contains(EIOConduitTypes.REDSTONE.get())) {
            return false;
        }

        RedstoneConduitData data = conduit.getBundle().getNodeFor(EIOConduitTypes.REDSTONE.get()).getConduitData().cast();
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
