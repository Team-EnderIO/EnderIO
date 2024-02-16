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
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EIONetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(EnderCore.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.play(SyncTravelDataPacket.ID, SyncTravelDataPacket::new,
            handler -> handler.client(ClientPayloadHandler.getInstance()::handleSyncTravelDataPacket));

        registrar.play(AddTravelTargetPacket.ID, AddTravelTargetPacket::new,
            handler -> handler.client(ClientPayloadHandler.getInstance()::handleAddTravelTarget));

        registrar.play(RemoveTravelTargetPacket.ID, RemoveTravelTargetPacket::new,
            handler -> handler.client(ClientPayloadHandler.getInstance()::handleRemoveTravelTarget));

        registrar.play(ServerToClientLightUpdate.ID, ServerToClientLightUpdate::new,
            handler -> handler.client(ClientPayloadHandler.getInstance()::handleLightUpdate));

        registrar.play(UpdateCoordinateSelectionNameMenuPacket.ID, UpdateCoordinateSelectionNameMenuPacket::new,
            handler -> handler.server(ServerPayloadHandler.getInstance()::handleCoordinateSelectionName));

        registrar.play(RequestTravelPacket.ID, RequestTravelPacket::new,
            handler -> handler.server(ServerPayloadHandler.getInstance()::handleTravelRequest));
    }
    
}
