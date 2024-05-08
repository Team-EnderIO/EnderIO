package com.enderio.base.common.network;

import com.enderio.base.common.travel.TravelSavedData;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleSyncTravelDataPacket(SyncTravelDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            TravelSavedData travelData = TravelSavedData.getTravelData(context.player().level());
            travelData.loadNBT(context.player().registryAccess(), packet.data());
        });
    }

    public void handleLightUpdate(ServerToClientLightUpdate message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().level.setBlock(message.pos(), message.state(), 3);
        });
    }

    public void handleAddTravelTarget(TravelTargetUpdatedPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            TravelSavedData travelData = TravelSavedData.getTravelData(level);
            travelData.setTravelTarget(level, packet.target());
        });
    }

    public void handleRemoveTravelTarget(TravelTargetRemovedPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            TravelSavedData travelData = TravelSavedData.getTravelData(level);
            travelData.removeTravelTargetAt(level, packet.pos());
        });
    }
}
