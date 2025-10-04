package com.enderio.enderio.common.foundation.network;

import com.enderio.enderio.api.travel.TravelTargetApi;
import com.enderio.enderio.common.content.travel.TravelTargetSavedData;
import com.enderio.enderio.common.foundation.network.packets.ClientboundSyncTravelDataPacket;
import com.enderio.enderio.common.foundation.network.packets.ClientboundTravelTargetRemovedPacket;
import com.enderio.enderio.common.foundation.network.packets.ClientboundTravelTargetUpdatedPacket;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleSyncTravelDataPacket(ClientboundSyncTravelDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            TravelTargetSavedData travelData = TravelTargetSavedData.getTravelData(context.player().level());
            travelData.loadNBT(context.player().registryAccess(), packet.data());
        });
    }

    public void handleAddTravelTarget(ClientboundTravelTargetUpdatedPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            TravelTargetApi.INSTANCE.set(level, packet.target());
        });
    }

    public void handleRemoveTravelTarget(ClientboundTravelTargetRemovedPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            TravelTargetApi.INSTANCE.removeAt(level, packet.pos());
        });
    }
}
