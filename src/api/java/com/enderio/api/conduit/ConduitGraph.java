package com.enderio.api.conduit;

import java.util.Collection;

public interface ConduitGraph<T extends ConduitData<T>> {
    Collection<ConduitNode<T>> getNodes();
}
