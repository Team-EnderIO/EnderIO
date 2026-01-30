package com.enderio.enderio.api.conduits.network;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.ConduitType;
import com.enderio.enderio.api.conduits.connection.config.IOConnectionConfig;
import com.enderio.enderio.api.conduits.network.node.ConduitNode;
import jdk.jfr.Experimental;
import net.minecraft.core.Holder;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Access to a network of conduits.
 * Provides a number of queries to help in the development of {@link ConduitTicker}'s.
 * All the queries are cached and are updated without the implementor having to worry about it.
 */
@ApiStatus.AvailableSince("8.0.0")
public interface ConduitNetwork {
    // Temporary during work on conduit changes.
    @Deprecated
    Holder<Conduit<?, ?>> conduit();

    ConduitType<?> conduitType();

    // TODO: is there a better way, or is preTick being exposed fine?
    @ApiStatus.Internal
    void beforeTicking();

    /**
     * @param gameTime the current game time.
     * @param tickOffset the tick offset (for spreading out network ticks)
     * @return all conduits that are capable of ticking currently.
     * @implNote This list will be cached, so it's reasonably fast to query.
     */
    @ApiStatus.Experimental
    List<Holder<Conduit<?, ?>>> getTickableConduits(long gameTime, int tickOffset);

    /**
     * @return the total number of nodes in this network.
     */
    int nodeCount();

    /**
     * @return whether the network has no nodes.
     */
    // TODO: Marked as experimental as idk if this is useful to a ticker, I might
    // remove this method.
    @ApiStatus.Experimental
    boolean isEmpty();

    /**
     * @param node the node to test for
     * @return whether the node belongs to this network.
     */
    // TODO: Marked as experimental as idk if this is useful to a ticker, I might
    // remove this method.
    @ApiStatus.Experimental
    boolean contains(ConduitNode node);

    /**
     * @return all nodes in this network.
     */
    Set<? extends ConduitNode> nodes();

    /**
     * @param node the node to query neighbors for.
     * @return all direct neighbors of the given node.
     */
    Set<? extends ConduitNode> neighbors(ConduitNode node);

    /**
     * @return all nodes that are in loaded and ticking chunks.
     */
    Collection<? extends ConduitNode> tickingNodes();

    /**
     * @return all nodes that are loaded, ticking, and are connected to a block.
     */
    Collection<? extends ConduitNode> blockEndpoints();

    /**
     * @return all connections to blocks that are loaded and ticking.
     */
    Collection<ConduitBlockConnection> blockConnections();

    /**
     * @param connection the connection to query from.
     * @return all nodes that are accessible from the given {@code connection}, this list will not include {@code connection}. This will be in the conduit's specified order.
     * @implNote The list is ordered by {@link Conduit#compareNodes(ConduitBlockConnection, ConduitBlockConnection, ConduitBlockConnection)}.
     */
    List<ConduitBlockConnection> blockConnectionsAccessibleFrom(ConduitBlockConnection connection);

    /**
     * For this query to yield results, the conduit's connection config must be derived from {@link IOConnectionConfig}.
     *
     * @return all channels that have connections in them
     */
    Set<DyeColor> allChannels();

    /**
     * For this query to yield results, the conduit's connection config must be derived from {@link IOConnectionConfig}.
     *
     * @return all the sending connections across all channels.
     * @implNote The list can be sorted using {@link Conduit#getGeneralConnectionComparator()}, but is often unordered.
     */
    List<ConduitBlockConnection> insertConnections();

    /**
     * For this query to yield results, the conduit's connection config must be derived from {@link IOConnectionConfig}.
     *
     * @param channel the channel to query for.
     * @return all the sending connections in the given {@code channel}.
     * @implNote The list can be sorted using {@link Conduit#getGeneralConnectionComparator()}, but is often unordered.
     */
    List<ConduitBlockConnection> insertConnections(DyeColor channel);

    /**
     * For this query to yield results, the conduit's connection config must be derived from {@link IOConnectionConfig}.
     *
     * @param insertConnection the insert connection to query from.
     * @return all the receiving connections that are accessible to the {@code insertConnection}, in the conduit's specified order.
     * @implNote The list is ordered by {@link Conduit#compareNodes(ConduitBlockConnection, ConduitBlockConnection, ConduitBlockConnection)}.
     */
    List<ConduitBlockConnection> extractConnectionsFrom(ConduitBlockConnection insertConnection);

    /**
     * For this query to yield results, the conduit's connection config must be derived from {@link IOConnectionConfig}.
     *
     * @return all the receiving connections across all channels.
     * @implNote The list is in no specific order.
     */
    List<ConduitBlockConnection> extractConnections();

    /**
     * For this query to yield results, the conduit's connection config must be derived from {@link IOConnectionConfig}.
     *
     * @param channel the channel to query for.
     * @return all the receiving connections in the given {@code channel}.
     * @implNote The list is in no specific order.
     */
    List<ConduitBlockConnection> extractConnections(DyeColor channel);

    /**
     * For this query to yield results, the conduit's connection config must be derived from {@link IOConnectionConfig}.
     *
     * @param extractConnection the extract connection to query from.
     * @return all the sending connections that are accessible to the {@code extractConnection}, in the conduit's specified order.
     * @implNote The list is ordered by {@link Conduit#compareNodes(ConduitBlockConnection, ConduitBlockConnection, ConduitBlockConnection)}.
     */
    List<ConduitBlockConnection> insertConnectionsFrom(ConduitBlockConnection extractConnection);

    /**
     * @param type the context type to check for.
     * @return whether the network has a context of the given type.
     */
    boolean hasContext(ConduitNetworkContextType<?> type);

    /**
     * @param type the type of the desired context.
     * @return the context of the given type, or null if there is no context or the type differs.
     */
    @Nullable
    <T extends ConduitNetworkContext<T>> T getContext(ConduitNetworkContextType<T> type);

    /**
     * @param type the type of the desired context.
     * @return the context of the given type (may be new).
     */
    <T extends ConduitNetworkContext<T>> T getOrCreateContext(ConduitNetworkContextType<T> type);
}
