package com.enderio.modconduits.mods.appeng;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;

public record GridNodeListener() implements IGridNodeListener<MEConduitNodeData> {

    public static GridNodeListener INSTANCE = new GridNodeListener();

    @Override
    public void onSaveChanges(MEConduitNodeData nodeOwner, IGridNode node) {
    }
}
