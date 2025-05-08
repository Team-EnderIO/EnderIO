package com.enderio.core.common.graph;

import org.jetbrains.annotations.Nullable;

public interface INetworkNode<TNet extends Network<TNet, TNode>, TNode extends INetworkNode<TNet, TNode>> {

    /**
     * @return whether this node has a valid network or not.
     */
    boolean isValid();

    /**
     * @throws IllegalStateException if the node is invalid (i.e. is an orphan)
     * @return the network this node is a member of.
     */
    TNet getNetwork();

    /**
     * Do not call this method directly.
     * @param network The network this node is a member of, or null if this node is being invalidated.
     */
    void setNetwork(@Nullable TNet network);
}
