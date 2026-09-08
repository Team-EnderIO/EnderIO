package com.enderio.modded_conduits.common.modules.appeng;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IGridNodeListener;
import com.enderio.enderio.api.conduits.bundle.ConduitBundle;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlock;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;

public record GridNodeListener() implements IGridNodeListener<MEConduitNodeData> {

    public static final GridNodeListener INSTANCE = new GridNodeListener();

    @Override
    public void onSaveChanges(MEConduitNodeData nodeOwner, IGridNode node) {
    }

    @Override
    public void onInWorldConnectionChanged(MEConduitNodeData nodeOwner, IGridNode node) {
        // ME Conduits now offer connection points through non-disabled sides and then waits for a grid node to connect to us.
        // Once a grid node is fully available, this simply re-checks the connections to ensure we show the correct connected state.
        var worldPos = nodeOwner.pos();
        if (worldPos == null) {
            return;
        }

        if (node.getLevel().getBlockEntity(worldPos) instanceof ConduitBundleBlockEntity conduitBundle) {
            conduitBundle.updateConnections(node.getLevel(), false);
        }
    }
}
