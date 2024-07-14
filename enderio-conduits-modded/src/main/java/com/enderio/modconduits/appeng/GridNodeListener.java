package com.enderio.modconduits.appeng;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;

public record GridNodeListener() implements IGridNodeListener<ConduitInWorldGridNodeHost> {

    public static GridNodeListener INSTANCE = new GridNodeListener();

    @Override
    public void onSaveChanges(ConduitInWorldGridNodeHost nodeOwner, IGridNode node) {
    }
}
