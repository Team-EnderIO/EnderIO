package com.enderio.base.common.network;

import com.enderio.base.common.travel.TravelSavedData;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ClientPayloadHandler {
    private static final ClientPayloadHandler INSTANCE = new ClientPayloadHandler();

    public static ClientPayloadHandler getInstance() {
        return INSTANCE;
    }

    public void handleSyncTravelDataPacket(SyncTravelDataPacket packet, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                TravelSavedData travelData = TravelSavedData.getTravelData(null);
                travelData.loadNBT(packet.data());
            });
    }

    public void handleLightUpdate(ServerToClientLightUpdate message, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                Minecraft.getInstance().level.setBlock(message.pos(), message.state(), 3);
            });
    }

    public void handleAddTravelTarget(AddTravelTargetPacket packet, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                context.level().ifPresent(level -> {
                    TravelSavedData travelData = TravelSavedData.getTravelData(level);
                    travelData.addTravelTarget(level, packet.target());
                });
            });
    }

    public void handleRemoveTravelTarget(RemoveTravelTargetPacket packet, PlayPayloadContext context) {
        context.workHandler()
            .submitAsync(() -> {
                context.level().ifPresent(level -> {
                    TravelSavedData travelData = TravelSavedData.getTravelData(level);
                    travelData.removeTravelTargetAt(level, packet.pos());
                });
            });
    }
}
