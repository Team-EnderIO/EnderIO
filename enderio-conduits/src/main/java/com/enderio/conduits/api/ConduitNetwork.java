package com.enderio.conduits.api;

import java.util.Collection;

public interface ConduitNetwork extends ConduitNetworkContextAccessor {
    Collection<ConduitNode> getNodes();
}
