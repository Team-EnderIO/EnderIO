package com.enderio.conduits.common.integrations.ae2;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;

public record GridNodeListener() implements IGridNodeListener<AE2InWorldConduitNodeHost> {

    @Override
    public void onSecurityBreak(AE2InWorldConduitNodeHost nodeOwner, IGridNode node) {
        nodeOwner.onSecurityBreach();
    }

    @Override
    public void onSaveChanges(AE2InWorldConduitNodeHost nodeOwner, IGridNode node) {
    }
}
