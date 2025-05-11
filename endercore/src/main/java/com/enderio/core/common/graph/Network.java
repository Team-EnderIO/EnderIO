package com.enderio.core.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

/**
 * A graph-backed network structure, with support for merging and splitting networks.
 *
 * @param <TNet> The network implementation type.
 * @param <TNode> The node type for this network.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class Network<TNet extends Network<TNet, TNode>, TNode extends INetworkNode<TNet, TNode>> {

    // The underlying graph.
    // Uses stable element ordering to ensure consistent behavior when serializing
    // the graph.
    final MutableGraph<TNode> graph = GraphBuilder.undirected()
            .allowsSelfLoops(false)
            .nodeOrder(ElementOrder.stable())
            .incidentEdgeOrder(ElementOrder.stable())
            .build();

    // Whether the network has been discarded due to a merge.
    boolean isDiscarded;

    /**
     * Create a network with a single starting node.
     * @param initialNode The initial node. This node must not be attached to any other graphs.
     */
    public Network(TNode initialNode) {
        this(List.of(initialNode), List.of());
    }

    /**
     * Create a network with a pre-configured set of edges.
     * @param nodes All the nodes in this network. None may be attached to another network.
     * @param edges All the edges linking nodes together. Edges must reflect the nodes in the {@code nodes} list.
     */
    public Network(List<TNode> nodes, List<Pair<TNode, TNode>> edges) {
        // Ensure there's at least one edge.
        Preconditions.checkArgument(!nodes.isEmpty(), "Cannot create a network with no nodes.");
        Preconditions.checkArgument(nodes.stream().noneMatch(INetworkNode::isValid),
                "Some nodes are already in networks.");

        // Special case for a single starting node
        if (nodes.size() == 1) {
            Preconditions.checkArgument(edges.isEmpty(), "A single node cannot have any edges.");
            var node = nodes.getFirst();
            graph.addNode(node);
            node.setNetwork(self());
            onNodeAdded(node);
            return;
        }

        // If some of these nodes are already in networks, the caller should use
        // connect/connectMany instead.
        Preconditions.checkArgument(
                edges.stream().allMatch(e -> nodes.contains(e.getFirst()) && nodes.contains(e.getSecond())),
                "Some edges reference nodes that were not included in the node list.");

        // Add all nodes
        for (var node : nodes) {
            graph.addNode(node);
            node.setNetwork(self());
            onNodeAdded(node);
        }

        // Add all edges (and nodes)
        for (var edge : edges) {
            graph.putEdge(edge.getFirst(), edge.getSecond());
            onNodesConnected(edge.getFirst(), edge.getSecond());
        }
    }

    /**
     * Create a network with a pre-configured set of edges, represented as pairs of indices into {@code nodes}.
     * This is intended for use during serialization/deserialization.
     * @param nodes All the nodes in this network. None may be attached to another network.
     * @param edges All the edges linking nodes together, indexing nodes in {@code nodes}.
     */
    public Network(List<TNode> nodes, IndexedEdgeList edges) {
        this(nodes, edges.expand(nodes));
    }

    /**
     * This constructor is used when merging graphs and thus can be initialized with no nodes.
     * This constructor should never be made public. Use {@link Network#Network(INetworkNode)}} instead.
     */
    protected Network() {
    }

    // region Queries

    /**
     * @return whether this network is valid (i.e., not discarded).
     */
    public final boolean isValid() {
        return !isDiscarded;
    }

    /**
     * @return whether this network has been discarded and should not be used.
     */
    public final boolean isDiscarded() {
        return isDiscarded;
    }

    public final int nodeCount() {
        ensureNotDiscarded();
        return graph.nodes().size();
    }

    public final boolean isEmpty() {
        ensureNotDiscarded();
        return graph.nodes().isEmpty();
    }

    public final boolean contains(TNode node) {
        ensureNotDiscarded();
        return graph.nodes().contains(node);
    }

    public final Set<TNode> nodes() {
        ensureNotDiscarded();
        return graph.nodes();
    }

    public final Set<TNode> neighbors(TNode node) {
        ensureNotDiscarded();
        return graph.adjacentNodes(node);
    }

    public final Stream<Pair<TNode, TNode>> edges() {
        return graph.edges().stream().map(e -> Pair.of(e.nodeU(), e.nodeV()));
    }

    // endregion

    // region Add/Remove Nodes + Connections

    public final void connect(TNode node, TNode neighbor) {
        connect(node, neighbor, null);
    }

    public final void connect(TNode node, TNode neighbor, @Nullable Consumer<TNet> onNetworkDiscard) {
        ensureNotDiscarded();

        Preconditions.checkArgument(node.isValid(), "Node is not valid");
        Preconditions.checkArgument(node != neighbor, "Cannot connect a node to itself.");
        Preconditions.checkArgument(contains(node), "Node is not in this graph.");

        if (neighbor.isValid()) {
            // Merge the network if it differs from this.
            mergeIfDifferent(neighbor.getNetwork(), onNetworkDiscard);
        } else {
            // Introduce this node to this network.
            neighbor.setNetwork(self());
            graph.addNode(neighbor);
            onNodeAdded(neighbor);
        }

        // Add the edge (and neighbor node if it is new)
        graph.putEdge(node, neighbor);
        onNodesConnected(node, neighbor);
    }

    public final void connectMany(TNode node, List<TNode> neighbors) {
        connectMany(node, neighbors, null);
    }

    public final void connectMany(TNode node, List<TNode> neighbors, @Nullable Consumer<TNet> onNetworkDiscard) {
        ensureNotDiscarded();

        Preconditions.checkArgument(node.isValid(), "Node is not valid");
        Preconditions.checkArgument(contains(node), "Node is not in this graph.");

        // Find all networks within the neighbors
        // We don't immediately merge to avoid creating invalid state if preconditions
        // fail.
        Set<TNet> otherNetworks = Sets.newHashSet();
        for (var neighbor : neighbors) {
            Preconditions.checkArgument(neighbor != node, "Cannot connect a node to itself.");
            if (neighbor.isValid() && neighbor.getNetwork() != this) {
                otherNetworks.add(neighbor.getNetwork());
            }
        }

        // If there are any other networks, merge them into this one
        for (var network : otherNetworks) {
            mergeIfDifferent(network, onNetworkDiscard);
        }

        for (var neighbor : neighbors) {
            // Any neighbor who is not yet member of a graph must be added to this one.
            if (!neighbor.isValid()) {
                neighbor.setNetwork(self());
                graph.addNode(neighbor);
                onNodeAdded(neighbor);
            }

            // Add edges (and neighbor nodes if not present)
            graph.putEdge(node, neighbor);
            onNodesConnected(node, neighbor);
        }
    }

    protected void onNodeAdded(TNode node) {
    }

    protected void onNodesConnected(TNode node1, TNode node2) {
    }

    public final void disconnect(TNode node1, TNode node2) {
        disconnect(node1, node2, null);
    }

    public final void disconnect(TNode node1, TNode node2, @Nullable Consumer<TNet> onNetworkCreated) {
        ensureNotDiscarded();

        Preconditions.checkArgument(node1 != node2, "Cannot disconnect a node from itself");
        Preconditions.checkArgument(node1.isValid(), "Node 1 is not valid");
        Preconditions.checkArgument(node2.isValid(), "Node 2 is not valid");
        Preconditions.checkArgument(contains(node1), "Node 1 does not belong to this network");
        Preconditions.checkArgument(contains(node2), "Node 2 does not belong to this network");

        // Remove edge between these two nodes
        graph.removeEdge(node1, node2);

        // Split networks if necessary
        splitIfRequired(onNetworkCreated);

        onNodesDisconnected(node1, node2);
    }

    protected void onNodesDisconnected(TNode node1, TNode node2) {
    }

    public final void remove(TNode node) {
        remove(node, null);
    }

    public final void remove(TNode node, @Nullable Consumer<TNet> onNetworkCreated) {
        ensureNotDiscarded();

        Preconditions.checkArgument(node.isValid(), "Node is not valid");
        Preconditions.checkArgument(contains(node), "Node does not belong to this network");

        // Remove the node (also removes its edges)
        graph.removeNode(node);

        // Invalidate the node
        node.setNetwork(null);

        // Split networks
        splitIfRequired(onNetworkCreated);

        // Fire remove event
        onNodeRemoved(node);
    }

    protected void onNodeRemoved(TNode node) {
    }

    // endregion

    // region Internal Behaviors

    private void mergeIfDifferent(TNet other, @Nullable Consumer<TNet> onNetworkDiscard) {
        // Do not attempt to merge with ourself.
        if (other == this) {
            return;
        }

        // Bring all nodes and edges into this network.
        other.graph.nodes().forEach(graph::addNode);
        other.graph.edges().forEach(graph::putEdge);

        // Move nodes to this network.
        for (var node : other.graph.nodes()) {
            node.setNetwork(self());
        }

        // Handle the merge.
        onMerged(other);

        // Mark the other network as discarded.
        other.isDiscarded = true;
        if (onNetworkDiscard != null) {
            onNetworkDiscard.accept(other);
        }
    }

    protected void onMerged(TNet other) {
        // This is where you'd merge any additional context, for example.
    }

    private void splitIfRequired(@Nullable Consumer<TNet> onNetworkCreated) {
        if (graph.nodes().isEmpty()) {
            return;
        }

        var remaining = Sets.newHashSet(graph.nodes());
        var seen = Sets.newHashSet();
        Queue<TNode> toVisit = Queues.newArrayDeque();

        // Add the starting node for this graph
        var firstNode = remaining.iterator().next();
        toVisit.add(firstNode);
        seen.add(firstNode);
        remaining.remove(firstNode);

        // Iterate over adjacent neighbours that will remain in this graph.
        while (!toVisit.isEmpty()) {
            var node = toVisit.poll();
            for (var neighbor : graph.adjacentNodes(node)) {
                if (!seen.contains(neighbor)) {
                    seen.add(neighbor);
                    toVisit.add(neighbor);
                    remaining.remove(neighbor);
                }
            }
        }

        // No new graphs, no further work to do.
        if (remaining.isEmpty()) {
            return;
        }

        // If any nodes remain, they are now disconnected - form graphs to replace them.
        Set<TNet> newGraphs = Sets.newHashSet();
        while (!remaining.isEmpty()) {
            // TODO: Potentially rework this to use the new constructor?
            var newGraph = createEmpty();

            firstNode = remaining.iterator().next();
            toVisit.add(firstNode);
            seen.add(firstNode);
            remaining.remove(firstNode);

            while (!toVisit.isEmpty()) {
                var node = toVisit.poll();
                for (var neighbor : graph.adjacentNodes(node)) {
                    if (!seen.contains(neighbor)) {
                        seen.add(neighbor);
                        toVisit.add(neighbor);
                        remaining.remove(neighbor);
                    }
                }

                // Add node and its edges to the new graph.
                newGraph.graph.addNode(node);
                graph.incidentEdges(node).forEach(newGraph.graph::putEdge);
                graph.removeNode(node);
                node.setNetwork(newGraph);
            }

            newGraphs.add(newGraph);
            if (onNetworkCreated != null) {
                onNetworkCreated.accept(newGraph);
            }
        }

        // Perform any additional split actions
        onGraphSplit(newGraphs);
    }

    protected void onGraphSplit(Set<TNet> newGraphs) {
        // This is where context splitting should occur.
    }

    // endregion

    // region Serialization Helpers

    protected static <TNet extends Network<TNet, TNode>, TNode extends INetworkNode<TNet, TNode>> Products.P2<RecordCodecBuilder.Mu<TNet>, List<TNode>, IndexedEdgeList> graphCodec(
            RecordCodecBuilder.Instance<TNet> instance, Codec<TNode> nodeCodec) {
        return instance.group(nodeCodec.listOf().fieldOf("nodes").forGetter(TNet::createNodeList),
                IndexedEdgeList.CODEC.fieldOf("edges").forGetter(TNet::createEdgeIndices));
    }

    public List<TNode> createNodeList() {
        return List.copyOf(nodes());
    }

    public IndexedEdgeList createEdgeIndices() {
        // Copy our edges into a list.
        // Because the underlying graph is set to be "stable", the order of this list
        // should be constant.
        var nodes = createNodeList();
        return new IndexedEdgeList(
                edges().map(pair -> Pair.of(nodes.indexOf(pair.getFirst()), nodes.indexOf(pair.getSecond()))).toList());
    }

    // Wrapper to get around type erasure issue in the constructors.
    public record IndexedEdgeList(List<Pair<Integer, Integer>> edges) {
        private static final Codec<Pair<Integer, Integer>> EDGE_CODEC = RecordCodecBuilder
                .create(inst -> inst
                        .group(Codec.INT.fieldOf("first").forGetter(Pair::getFirst),
                                Codec.INT.fieldOf("second").forGetter(Pair::getSecond))
                        .apply(inst, Pair::of));

        public static final Codec<IndexedEdgeList> CODEC = EDGE_CODEC.listOf()
                .xmap(IndexedEdgeList::new, IndexedEdgeList::edges);

        public <TNode extends INetworkNode<? extends Network<?, TNode>, TNode>> List<Pair<TNode, TNode>> expand(
                List<TNode> nodes) {
            return edges.stream()
                    .map(pair -> Pair.of(nodes.get(pair.getFirst()), nodes.get(pair.getSecond())))
                    .toList();
        }
    }

    // endregion

    protected abstract TNet createEmpty();

    protected final void ensureNotDiscarded() {
        Preconditions.checkState(!isDiscarded, "Cannot use a discarded network.");
    }

    // Generic fun and games :(
    @SuppressWarnings("unchecked")
    private TNet self() {
        return (TNet) this;
    }

}
