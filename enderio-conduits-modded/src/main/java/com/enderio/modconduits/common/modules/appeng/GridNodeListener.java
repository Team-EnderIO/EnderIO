package com.enderio.modconduits.common.modules.appeng;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;

public record GridNodeListener() implements IGridNodeListener<MEConduitNodeData> {

    public static final GridNodeListener INSTANCE = new GridNodeListener();

    @Override
    public void onSaveChanges(MEConduitNodeData nodeOwner, IGridNode node) {
    }
}
