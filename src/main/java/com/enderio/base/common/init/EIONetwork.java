package com.enderio.base.common.init;

import com.enderio.base.common.network.AddTravelTargetPacket;
import com.enderio.base.common.network.ClientPayloadHandler;
import com.enderio.base.common.network.RemoveTravelTargetPacket;
import com.enderio.base.common.network.RequestTravelPacket;
import com.enderio.base.common.network.ServerPayloadHandler;
import com.enderio.base.common.network.ServerToClientLightUpdate;
import com.enderio.base.common.network.SyncTravelDataPacket;
import com.enderio.base.common.network.UpdateCoordinateSelectionNameMenuPacket;
import com.enderio.core.EnderCore;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class EIONetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(EnderCore.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.playToClient(SyncTravelDataPacket.TYPE, SyncTravelDataPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleSyncTravelDataPacket);

        registrar.playToClient(AddTravelTargetPacket.TYPE, AddTravelTargetPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleAddTravelTarget);

        registrar.playToClient(RemoveTravelTargetPacket.TYPE, RemoveTravelTargetPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleRemoveTravelTarget);

        registrar.playToClient(ServerToClientLightUpdate.TYPE, ServerToClientLightUpdate.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleLightUpdate);

        registrar.playToServer(UpdateCoordinateSelectionNameMenuPacket.TYPE, UpdateCoordinateSelectionNameMenuPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleCoordinateSelectionName);

        registrar.playToServer(RequestTravelPacket.TYPE, RequestTravelPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleTravelRequest);
    }
    
}
