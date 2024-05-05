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
            TravelSavedData travelData = TravelSavedData.getTravelData(null);
            travelData.loadNBT(context.player().registryAccess(), packet.data());
        });
    }

    public void handleLightUpdate(ServerToClientLightUpdate message, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().level.setBlock(message.pos(), message.state(), 3);
        });
    }

    public void handleAddTravelTarget(AddTravelTargetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            TravelSavedData travelData = TravelSavedData.getTravelData(level);
            travelData.addTravelTarget(level, packet.target());
        });
    }

    public void handleRemoveTravelTarget(RemoveTravelTargetPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = context.player().level();
            TravelSavedData travelData = TravelSavedData.getTravelData(level);
            travelData.removeTravelTargetAt(level, packet.pos());
        });
    }
}
