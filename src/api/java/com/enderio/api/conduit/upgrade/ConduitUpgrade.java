package com.enderio.api.conduit.upgrade;

import com.enderio.api.conduit.ConduitType;

public interface ConduitUpgrade {
    boolean canApplyTo(ConduitType<?> type);
}
