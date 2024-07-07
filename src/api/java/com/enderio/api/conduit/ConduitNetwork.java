package com.enderio.api.conduit;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface ConduitNetwork extends ConduitNetworkContextAccessor {
    Collection<ConduitNode> getNodes();
}
