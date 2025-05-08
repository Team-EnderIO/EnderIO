package com.enderio.core.common.graph;

import org.jetbrains.annotations.Nullable;

public interface INetworkNode<TNet extends Network<TNet, TNode>, TNode extends INetworkNode<TNet, TNode>> {
    @Nullable TNet getNetwork();

    /**
     * Do not call this method directly.
     */
    void setNetwork(@Nullable TNet network);
}
