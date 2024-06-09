package com.enderio.api.conduit;

import java.util.Collection;

public interface GraphAccessor<T extends ConduitData<T>> {
    Collection<ConduitNode<T>> getNodes();
}
