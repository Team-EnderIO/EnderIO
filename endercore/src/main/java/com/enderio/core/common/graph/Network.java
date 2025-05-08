package com.enderio.core.common.graph;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * A graph-backed network structure, with support for merging and splitting networks.
 *
 * @param <TNet> The network implementation type.
 * @param <TNode> The node type for this network.
 */
@SuppressWarnings("UnstableApiUsage")
public abstract class Network<TNet extends Network<TNet, TNode>, TNode extends INetworkNode<TNet, TNode>> {

    // This network's graph.
    final MutableGraph<TNode> graph = GraphBuilder.undirected()
            .allowsSelfLoops(false)
            .nodeOrder(ElementOrder.unordered())
            .build();

    // Whether the network has been discarded due to a merge.
    boolean isDiscarded;

    protected Network(TNode initialNode) {
        graph.addNode(initialNode);
        initialNode.setNetwork(self());
        onNetworkChanged();
    }

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

    // endregion

    // region Add/Remove Nodes + Connections

    public final void connect(TNode node, TNode neighbor) {
        connect(node, neighbor, null);
    }

    public final void connect(TNode node, TNode neighbor, @Nullable Consumer<TNet> onNetworkDiscard) {
        ensureNotDiscarded();

        Preconditions.checkArgument(node != neighbor, "Cannot connect a node to itself.");
        Preconditions.checkArgument(contains(node), "Node is not in this graph.");

        // Merge any potential network.
        var neighborNetwork = neighbor.getNetwork();
        if (neighborNetwork != null && neighborNetwork != this) {
            mergeWith(neighborNetwork, onNetworkDiscard);
        } else {
            // Ensure the neighbor is set to this network.
            neighbor.setNetwork(self());
        }

        graph.putEdge(node, neighbor);
        onNetworkChanged();
    }

    public final void connectMany(TNode node, List<TNode> neighbors, @Nullable Consumer<TNet> onNetworkDiscard) {
        ensureNotDiscarded();

        Preconditions.checkArgument(contains(node), "Node is not in this graph.");

        // Find all graphs within the neighbors
        Set<TNet> otherGraphs = Sets.newHashSet();
        for (var neighbor : neighbors) {
            Preconditions.checkArgument(neighbor != node, "Cannot connect a node to itself.");

            var neighborNetwork = neighbor.getNetwork();
            if (neighborNetwork != null && neighborNetwork != this) {
                otherGraphs.add(neighborNetwork);
            }
        }

        // If there are any other graphs, merge them into this one
        if (!otherGraphs.isEmpty()) {
            // Merge with all graphs
            for (var graph : otherGraphs) {
                mergeWith(graph, onNetworkDiscard);
            }
        }

        for (var neighbor : neighbors) {
            // Any neighbors who had no graph get set to this network.
            // For other neighbors, this was done during merging.
            if (neighbor.getNetwork() == null) {
                neighbor.setNetwork(self());
            }

            // Add edges
            graph.putEdge(node, neighbor);
        }

        onNetworkChanged();
    }

    public final void disconnect(TNode node1, TNode node2, @Nullable Consumer<TNet> onNetworkCreated) {
        ensureNotDiscarded();

        Preconditions.checkArgument(node1.getNetwork() == this, "Node 1 does not belong to this network");
        Preconditions.checkArgument(node2.getNetwork() == this, "Node 2 does not belong to this network");
        Preconditions.checkArgument(node1 != node2, "Cannot disconnect a node from itself");

        // Remove edge between these two nodes
        graph.removeEdge(node1, node2);
        onNetworkChanged();

        // Split networks if necessary
        splitIfRequired(onNetworkCreated);
    }

    public final void remove(TNode node) {
        remove(node, null);
    }

    public final void remove(TNode node, @Nullable Consumer<TNet> onNetworkCreated) {
        ensureNotDiscarded();

        Preconditions.checkArgument(node.getNetwork() == this, "Node does not belong to this network");

        // Remove the node (also removes its edges)
        node.setNetwork(null);
        graph.removeNode(node);
        onNetworkChanged();

        // Split networks
        splitIfRequired(onNetworkCreated);
    }

    protected void onNetworkChanged() {
    }

    // endregion

    // region Internal Behaviors

    private void mergeWith(TNet other, @Nullable Consumer<TNet> onNetworkDiscard) {
        // No-op if we try to merge ourself.
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
        onNetworkChanged();

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
            firstNode = remaining.iterator().next();
            toVisit.add(firstNode);
            seen.add(firstNode);
            remaining.remove(firstNode);

            var newGraph = createEmpty();
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
                graph.incidentEdges(node).forEach(newGraph.graph::putEdge);
                graph.removeNode(node);
                node.setNetwork(newGraph);
            }

            if (onNetworkCreated != null) {
                onNetworkCreated.accept(newGraph);
            }
        }

        // Perform any additional split actions
        onGraphSplit(newGraphs);

        // Fire network on changed events
        newGraphs.forEach(TNet::onNetworkChanged);
        onNetworkChanged();
    }

    protected void onGraphSplit(Set<TNet> newGraphs) {
        // This is where context splitting should occur.
    }

    // endregion

    protected abstract TNet createEmpty();

    protected final void ensureNotDiscarded() {
        Preconditions.checkState(!isDiscarded, "Cannot use a discarded network.");
    }

    // Generic fun and games :(
    @SuppressWarnings("unchecked")
    private final TNet self() {
        return (TNet) this;
    }

}
