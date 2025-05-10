package com.enderio.conduits.api.network;

import com.enderio.conduits.api.network.node.IConduitNode;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface IConduitNetwork {
    int nodeCount();
    boolean isEmpty();
    boolean contains(IConduitNode node);
    Set<? extends IConduitNode> nodes();
    Set<? extends IConduitNode> neighbors(IConduitNode node);

    Collection<? extends IConduitNode> loadedNodes();
    Collection<? extends IConduitNode> blockEndpoints();
    Collection<ConduitBlockConnection> blockConnections();
    List<ConduitBlockConnection> blockConnectionsAccessibleFrom(ConduitBlockConnection connection);
    Set<DyeColor> allChannels();
    List<ConduitBlockConnection> sendingConnections();
    List<ConduitBlockConnection> sendingConnections(DyeColor channel);
    List<ConduitBlockConnection> receivingConnectionsFrom(ConduitBlockConnection sender);
    List<ConduitBlockConnection> receivingConnections();
    List<ConduitBlockConnection> receivingConnections(DyeColor channel);
    List<ConduitBlockConnection> sendingConnectionsFrom(ConduitBlockConnection receiver);

    boolean hasContext(ConduitNetworkContextType<?> type);
    @Nullable
    <T extends ConduitNetworkContext<T>> T getContext(ConduitNetworkContextType<T> type);
    <T extends ConduitNetworkContext<T>> T getOrCreateContext(ConduitNetworkContextType<T> type);
}
