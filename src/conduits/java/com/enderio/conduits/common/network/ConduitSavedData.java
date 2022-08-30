package com.enderio.conduits.common.network;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import com.enderio.conduits.common.blockentity.TieredConduit;
import com.enderio.core.common.blockentity.ColorControl;
import com.enderio.EnderIO;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mojang.datafixers.util.Pair;
import dev.gigaherz.graph3.Graph;
import dev.gigaherz.graph3.GraphObject;
import dev.gigaherz.graph3.Mergeable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class ConduitSavedData extends SavedData {

    ListMultimap<IConduitType, Graph<Mergeable.Dummy>> networks = ArrayListMultimap.create();

    public static ConduitSavedData get(ServerLevel level) {
        ConduitSavedData ret = level.getDataStorage().computeIfAbsent(ConduitSavedData::new, ConduitSavedData::new, "enderio_conduit_network");
        ret.giveBlocksData(level);
        return ret;
    }

    private ConduitSavedData() {

    }

    // Deserialization
    private ConduitSavedData(CompoundTag nbt) {
        EnderIO.LOGGER.info("Conduit network deserialization started");
        long start = System.currentTimeMillis();
        ListTag graphsTag = nbt.getList("graphs", Tag.TAG_COMPOUND);
        for (Tag tag : graphsTag) {
            CompoundTag typedGraphTag = (CompoundTag) tag;
            ResourceLocation type = new ResourceLocation(typedGraphTag.getString("type"));

            if (ConduitTypes.getRegistry().containsKey(type)) {
                IConduitType value = ConduitTypes.getRegistry().getValue(type);
                ListTag graphsForTypeTag = typedGraphTag.getList("graphs", Tag.TAG_COMPOUND);
                for (Tag tag1 : graphsForTypeTag) {
                    CompoundTag graphTag = (CompoundTag) tag1;

                    ListTag graphObjectsTag = graphTag.getList("graphObjects", Tag.TAG_LONG);
                    ListTag graphConnectionsTag = graphTag.getList("graphConnections", Tag.TAG_COMPOUND);

                    List<NodeIdentifier> graphObjects = new ArrayList<>();
                    List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();


                    for (Tag tag2 : graphObjectsTag) {
                        NodeIdentifier node = new NodeIdentifier(BlockPos.of(((LongTag)tag2).getAsLong()));
                        graphObjects.add(node);
                    }

                    for (Tag tag2: graphConnectionsTag) {
                        CompoundTag connectionTag = (CompoundTag) tag2;
                        connections.add(new Pair<>(graphObjects.get(connectionTag.getInt("0")), graphObjects.get(connectionTag.getInt("1"))));
                    }

                    NodeIdentifier graphObject = graphObjects.get(0);
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
              ┠ graphs (list) // One type of conduits' graphs
              ┃   ┠ [index]
              ┃   ┃   ┠ graphConnections (list)
              ┃   ┃   ┃   ┖ [index]
              ┃   ┃   ┃       ┠ 0: [first object's index]
              ┃   ┃   ┃       ┖ 1: [second object's index]
              ┃   ┃   ┖ graphObjects (list)
              ┃   ┃       ┖ [index]: long (representing BlockPos.asLong())
              ┃   ┖ [next index]
              ┃       ┖ ...
              ┖ type: [conduit type] (ex. "enderio:power3")
     */

    // Serialization
    @Override
    public CompoundTag save(CompoundTag nbt) {
        EnderIO.LOGGER.info("Conduit network serialization started");
        long start = System.currentTimeMillis();
        ListTag graphsTag = new ListTag();
        for (IConduitType type: networks.keySet()) {
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

            if (graphObject instanceof NodeIdentifier nodeIdentifier) {
                graphObjectsTag.add(LongTag.valueOf(nodeIdentifier.getPos().asLong()));
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
            Graph.connect(neighbor, object);
        }

        connections = connections.stream().filter(v -> !filteredConnections.contains(v)).toList();
        if (!connections.isEmpty()) {
            merge(connections.get(0).getFirst(), connections);
        }
    }

    private boolean dataGiven = false;

    public void giveBlocksData(ServerLevel level) {
        if (dataGiven) return;
        dataGiven = true;

        for (IConduitType type: this.networks.keySet()) {
            List<Graph<Mergeable.Dummy>> graphs = this.networks.get(type);
            if (graphs.isEmpty()) continue;

            for (Graph<Mergeable.Dummy> graph: graphs) {
                Collection<GraphObject<Mergeable.Dummy>> objects = graph.getObjects();
                if (objects.isEmpty()) continue;

                for (var object : objects) {
                    if (object instanceof NodeIdentifier nodeIdentifier) {
                        var blockEntity = level.getBlockEntity(nodeIdentifier.getPos());
                        if (blockEntity instanceof ConduitBlockEntity conduit) {
                            conduit.getBundle().setNodeFor(type, nodeIdentifier);
                        }
                    }
                }
            }
        }
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
        for (var entry : networks.entries()) {
            //tick four times per second
            if (serverLevel.getGameTime() % 5 == ConduitTypes.getRegistry().getID(entry.getKey()) % 5) {
                //TODO put that into the conduit instance
                if (entry.getKey() instanceof TieredConduit tieredConduit && tieredConduit.getType().equals(new ResourceLocation("forge", "power"))) {
                    ListMultimap<ColorControl, ConnectorPos> inputs = ArrayListMultimap.create();
                    ListMultimap<ColorControl, ConnectorPos> outputs = ArrayListMultimap.create();
                    for (GraphObject<Mergeable.Dummy> object : entry.getValue().getObjects()) {
                        if (object instanceof NodeIdentifier nodeIdentifier) {
                            for (Direction direction: Direction.values()) {
                                if (serverLevel.isLoaded(nodeIdentifier.getPos()) && serverLevel.shouldTickBlocksAt(nodeIdentifier.getPos())) {
                                    nodeIdentifier.getIOState(direction).ifPresent(ioState -> {
                                        ioState.in().ifPresent(color -> inputs.get(color).add(new ConnectorPos(nodeIdentifier.getPos(), direction)));
                                        ioState.out().ifPresent(color -> outputs.get(color).add(new ConnectorPos(nodeIdentifier.getPos(), direction)));
                                    });
                                }
                            }
                        }
                    }
                    for (ColorControl color: inputs.keySet()) {
                        Map<ConnectorPos, IEnergyStorage> inputCaps = new HashMap<>();
                        for (ConnectorPos inputAt : inputs.get(color)) {
                            checkFor(serverLevel, inputAt, CapabilityEnergy.ENERGY).ifPresent(energy -> inputCaps.put(inputAt, energy));
                        }
                        if (inputCaps.isEmpty())
                            continue;
                        Map<ConnectorPos, IEnergyStorage> outputCaps = new HashMap<>();
                        for (ConnectorPos outputAt : outputs.get(color)) {
                            checkFor(serverLevel, outputAt, CapabilityEnergy.ENERGY).ifPresent(energy -> outputCaps.put(outputAt, energy));
                        }
                        for (Map.Entry<ConnectorPos, IEnergyStorage> inputEntry : inputCaps.entrySet()) {
                            int extracted = inputEntry.getValue().extractEnergy(tieredConduit.getTier(), true);
                            int inserted = 0;
                            for (Map.Entry<ConnectorPos, IEnergyStorage> outputEntry : outputCaps.entrySet()) {
                                inserted += outputEntry.getValue().receiveEnergy(extracted - inserted, false);
                                if (inserted == extracted)
                                    break;
                            }
                            inputEntry.getValue().extractEnergy(inserted, false);
                        }
                    }
                }
            }
        }
    }

    public static void addPotentialGraph(IConduitType type, Graph<Mergeable.Dummy> graph, ServerLevel level) {
        get(level).addPotentialGraph(type, graph);
    }

    private void addPotentialGraph(IConduitType type, Graph<Mergeable.Dummy> graph) {
        if (!networks.get(type).contains(graph)) {
            networks.get(type).add(graph);
        }
    }

    private static <T> Optional<T> checkFor(ServerLevel level, ConnectorPos pos, Capability<T> cap) {
        BlockEntity blockEntity = level.getBlockEntity(pos.move());
        if (blockEntity != null)
            return blockEntity.getCapability(cap, pos.dir().getOpposite()).resolve();
        return Optional.empty();
    }

    private static record ConnectorPos(BlockPos pos, Direction dir) {
        private BlockPos move() {
            return pos.relative(dir);
        }
    }
}
