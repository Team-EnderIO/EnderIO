package com.enderio.conduits.common.network;

import com.enderio.conduits.common.blockentity.ConduitBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ConduitClientPayloadHandler {
    private static final ConduitClientPayloadHandler INSTANCE = new ConduitClientPayloadHandler();

    public static ConduitClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleConduitConnectionState(final C2SSetConduitConnectionState packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            BlockEntity be = level.getBlockEntity(packet.pos());
            if (be instanceof ConduitBlockEntity conduitBlockEntity) {
                conduitBlockEntity.handleConnectionStateUpdate(packet.direction(), packet.conduitType(), packet.connectionState());
            }
        });
    }

    public void handleConduitExtendedData(final C2SSetConduitExtendedData packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            BlockEntity be = level.getBlockEntity(packet.pos());
            if (be instanceof ConduitBlockEntity conduitBlockEntity) {
                conduitBlockEntity.handleExtendedDataUpdate(packet.conduitType(), packet.extendedConduitData());
            }
        });
    }
}
