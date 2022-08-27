package com.enderio.conduits.common.network;

import com.enderio.api.conduit.ConduitTypes;
import com.enderio.api.conduit.IConduitType;
import com.enderio.conduits.common.blockentity.TieredConduit;
import com.enderio.core.common.blockentity.ColorControl;
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
        return level.getDataStorage().computeIfAbsent(ConduitSavedData::new, ConduitSavedData::new, "enderio:conduit_network");
    }

    private ConduitSavedData() {

    }

    // Deserialization
    private ConduitSavedData(CompoundTag nbt) {
        // All conduits' graphs, separated by type
        ListTag graphsTag = nbt.getList("graphs", Tag.TAG_COMPOUND);
        for (Tag tag : graphsTag) {
            // One type of conduit's graphs and type
            CompoundTag typedGraphTag = (CompoundTag) tag;
            ResourceLocation type = new ResourceLocation(typedGraphTag.getString("type"));

            if (ConduitTypes.getRegistry().containsKey(type)) {
                // The conduit type
                IConduitType value = ConduitTypes.getRegistry().getValue(type);

                // List of graphs extracted from the CompoundTag
                ListTag graphsForTypeTag = typedGraphTag.getList("graphs", Tag.TAG_COMPOUND);
                for (Tag tag1 : graphsForTypeTag) {
                    CompoundTag graphTag = (CompoundTag) tag1;

                    ListTag graphObjectsTag = graphTag.getList("graphObjects", Tag.TAG_LONG);
                    ListTag graphConnectionsTag = graphTag.getList("graphConnections", Tag.TAG_COMPOUND);

                    List<NodeIdentifier> graphObjects = new ArrayList<>();
                    List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();

                    // Add all the objects to a list
                    for (Tag tag2 : graphObjectsTag) {
                        graphObjects.add(new NodeIdentifier(BlockPos.of(((LongTag)tag2).getAsLong())));
                    }

                    // Add all the connections to a list
                    for (Tag tag2: graphConnectionsTag) {
                        CompoundTag connectionTag = (CompoundTag) tag2;
                        connections.add(new Pair<>(graphObjects.get(connectionTag.getInt("0")), graphObjects.get(connectionTag.getInt("1"))));
                    }

                    // Iterate over the graph objects to create the final graph of conduits of this type
                    for (NodeIdentifier graphObject: graphObjects) {
                        List<GraphObject<Mergeable.Dummy>> neighbors = getNeighbors(connections, graphObject);

                        // Integrates these objects into an existing graph, or creates a new graph if they're not connected
                        Graph.integrate(graphObject, neighbors);
                        // Remove redundant connections
                        removeConnections(connections, graphObject, neighbors);
                    }
                    networks.get(value).add(graphObjects.get(0).getGraph());
                }
            }
        }
    }

    // Serialization
    @Override
    public CompoundTag save(CompoundTag nbt) {
        // Tag that will be saved to the disk
        ListTag graphsTag = new ListTag();

        // Iterate over the types of conduits
        for (IConduitType type: networks.keySet()) {
            // Every graph of a single conduit type
            List<Graph<Mergeable.Dummy>> graphs = networks.get(type);
            if (graphs.isEmpty())
                continue;

            // Represents a list of graphs for one type of conduit
            CompoundTag typedGraphTag = new CompoundTag();

            // Insert the type of conduit into the tag
            typedGraphTag.putString("type", ConduitTypes.getRegistry().getKey(type).toString());

            // Insert the list of graphs
            ListTag graphsForTypeTag = new ListTag();
            typedGraphTag.put("graphs", graphsForTypeTag);

            // Iterate over the graphs of a single conduit type
            for (Graph<Mergeable.Dummy> graph: graphs) {
                if (graph.getObjects().isEmpty())
                    continue;

                // List of the graph's objects, each one represents a conduit
                List<GraphObject<Mergeable.Dummy>> graphObjects = new ArrayList<>(graph.getObjects());

                // List of the conduit's connections
                List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections = new ArrayList<>();

                // Represents one graph
                CompoundTag graphTag = new CompoundTag();

                ListTag graphObjectsTag = new ListTag();
                ListTag graphConnectionsTag = new ListTag();

                graphsForTypeTag.add(graphTag);
                graphTag.put("graphObjects", graphObjectsTag);
                graphTag.put("graphConnections", graphConnectionsTag);

                // Iterate over the objects
                for (GraphObject<Mergeable.Dummy> graphObject: graphObjects) {
                    // Iterate over the objects' neighbors
                    for (GraphObject<Mergeable.Dummy> neighbour : graph.getNeighbours(graphObject)) {
                        // Create a connection between two neighboring conduits
                        Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection = new Pair<>(graphObject, neighbour);
                        if (!containsConnection(connections, connection)) {
                            // Add connection if the connection doesn't exist yet
                            connections.add(connection);
                        }
                    }

                    if (graphObject instanceof NodeIdentifier nodeIdentifier) {
                        // Add a conduit object represented by its block position in the world
                        graphObjectsTag.add(LongTag.valueOf(nodeIdentifier.getPos().asLong()));
                    } else {
                        throw new ClassCastException("graphObject was not of type nodeIdentifier");
                    }
                }

                for (Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection : connections) {
                    // Represents one connection between 2 objects
                    CompoundTag connectionTag = new CompoundTag();

                    connectionTag.put("0", IntTag.valueOf(graphObjects.indexOf(connection.getFirst())));
                    connectionTag.put("1", IntTag.valueOf(graphObjects.indexOf(connection.getSecond())));

                    // Add it to the connections
                    graphConnectionsTag.add(connectionTag);
                }
            }
            if (!graphsForTypeTag.isEmpty()) {
                // Add the graphs for one type of conduit
                graphsTag.add(typedGraphTag);
            }
        }

        // Put all the graphs into the final nbt tag to be saved
        nbt.put("graphs", graphsTag);
        return nbt;
    }

    private static boolean containsConnection(List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections, Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection) {
        return connections.contains(connection) || connections.contains(connection.swap());
    }
    private static List<GraphObject<Mergeable.Dummy>> getNeighbors(List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections, GraphObject<Mergeable.Dummy> of) {
        List<GraphObject<Mergeable.Dummy>> neighbors = new ArrayList<>();
        for (Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>> connection : connections) {
            if (connection.getFirst() == of)
                neighbors.add(connection.getSecond());
            if(connection.getSecond() == of)
                neighbors.add(connection.getFirst());
        }
        return neighbors;
    }

    /**
     * Removes redundant connections in a list of connections
     */
    private static void removeConnections(List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> connections, GraphObject<Mergeable.Dummy> node, List<GraphObject<Mergeable.Dummy>> neighborsOfNode) {
        List<Pair<GraphObject<Mergeable.Dummy>, GraphObject<Mergeable.Dummy>>> pairs = neighborsOfNode.stream().map(neighbor -> Pair.of(node, neighbor)).toList();
        connections.removeIf(connection -> containsConnection(pairs, connection));
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.START)
            return;
        if (event.level instanceof ServerLevel serverLevel) {
            for (var entry : get(serverLevel).networks.entries()) {
                //tick four times per second
                if (serverLevel.getGameTime() % 5 == ConduitTypes.getRegistry().getID(entry.getKey()) % 5) {

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
    }

    public static void addPotentialGraph(IConduitType type, Graph<Mergeable.Dummy> graph, ServerLevel level) {
        get(level).addPotentialGraph(type, graph);
    }

    private void addPotentialGraph(IConduitType type, Graph<Mergeable.Dummy> graph) {
        if (!networks.get(type).contains(graph)) {
            networks.get(type).add(graph);
        }
        // System.out.println(Arrays.toString(networks.entries().toArray()));
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
