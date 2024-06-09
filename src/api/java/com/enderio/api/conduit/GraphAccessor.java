package com.enderio.api.conduit;

import java.util.Collection;

public interface GraphAccessor<T extends ExtendedConduitData<T>> {
    Collection<ConduitNode<T>> getNodes();
}
