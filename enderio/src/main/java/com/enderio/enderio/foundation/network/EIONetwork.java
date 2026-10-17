package com.enderio.enderio.foundation.network;

import com.enderio.enderio.foundation.network.packets.ClientboundConduitExtraGuiDataPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundConduitListPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundSyncTravelDataPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundTravelTargetRemovedPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundTravelTargetUpdatedPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundCountFilterPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundCycleIOConfigPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundDoubleChannelPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundEnderfaceInteractPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundOpenConduitFilterMenu;
import com.enderio.enderio.foundation.network.packets.ServerboundRequestShortTravelPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundRequestTravelPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSetFluidFilterSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSetGhostSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSetItemFilterSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSyncProbeStatePacket;
import com.enderio.enderio.foundation.network.packets.ServerboundTimerFilterPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundToggleMagnetPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundTransferItemsPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundUpdateCoordinateSelectionNameMenuPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundUpdateCrafterTemplatePacket;
import com.enderio.enderio.foundation.network.packets.SetConduitConnectionConfigPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class EIONetwork {
    private static final String PROTOCOL_VERSION = "3";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // TODO: Tidy up this class.
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(ClientboundSyncTravelDataPacket.TYPE, ClientboundSyncTravelDataPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleSyncTravelDataPacket);

        registrar.playToClient(ClientboundTravelTargetUpdatedPacket.TYPE, ClientboundTravelTargetUpdatedPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleAddTravelTarget);

        registrar.playToClient(ClientboundTravelTargetRemovedPacket.TYPE, ClientboundTravelTargetRemovedPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleRemoveTravelTarget);

        registrar.playToServer(ServerboundUpdateCoordinateSelectionNameMenuPacket.TYPE, ServerboundUpdateCoordinateSelectionNameMenuPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleCoordinateSelectionName);

        registrar.playToServer(ServerboundRequestTravelPacket.TYPE, ServerboundRequestTravelPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleTravelRequest);

        registrar.playToServer(ServerboundRequestShortTravelPacket.TYPE, ServerboundRequestShortTravelPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleShortTravelRequest);

        registrar.playToServer(ServerboundSetFluidFilterSlotPacket.TYPE, ServerboundSetFluidFilterSlotPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleSetFluidFilterSlot);

        registrar.playToServer(ServerboundSetGhostSlotPacket.TYPE, ServerboundSetGhostSlotPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleSetGhostSlot);

        registrar.playToServer(ServerboundSetItemFilterSlotPacket.TYPE, ServerboundSetItemFilterSlotPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleSetItemFilterSlot);

        registrar.playToServer(ServerboundDoubleChannelPacket.TYPE, ServerboundDoubleChannelPacket.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handleDoubleChannelFilter);

        registrar.playToServer(ServerboundTimerFilterPacket.TYPE, ServerboundTimerFilterPacket.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handleTimerFilter);

        registrar.playToServer(ServerboundToggleMagnetPacket.TYPE, ServerboundToggleMagnetPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleMagnetToggle);

        registrar.playToServer(ServerboundCountFilterPacket.TYPE, ServerboundCountFilterPacket.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handleCountFilter);

        registrar.playToClient(ClientboundConduitExtraGuiDataPacket.TYPE, ClientboundConduitExtraGuiDataPacket.STREAM_CODEC,
            ConduitClientPayloadHandler.getInstance()::handle);

        registrar.playToClient(ClientboundConduitListPacket.TYPE, ClientboundConduitListPacket.STREAM_CODEC, ConduitClientPayloadHandler.getInstance()::handle);

        registrar.playBidirectional(SetConduitConnectionConfigPacket.TYPE, SetConduitConnectionConfigPacket.STREAM_CODEC,
            ConduitCommonPayloadHandler.getInstance()::handle, ConduitCommonPayloadHandler.getInstance()::handle);

        registrar.playToServer(ServerboundOpenConduitFilterMenu.TYPE, ServerboundOpenConduitFilterMenu.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handle);

        registrar.playToServer(ServerboundSyncProbeStatePacket.TYPE, ServerboundSyncProbeStatePacket.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handle);

        registrar.playToServer(ServerboundUpdateCrafterTemplatePacket.TYPE, ServerboundUpdateCrafterTemplatePacket.STREAM_CODEC,
            MachinePayloadHandler.Server.getInstance()::updateCrafterTemplate);

        registrar.playToServer(ServerboundCycleIOConfigPacket.TYPE, ServerboundCycleIOConfigPacket.STREAM_CODEC,
            MachinePayloadHandler.Server.getInstance()::handleCycleIOConfigPacket);

        registrar.playToServer(ServerboundEnderfaceInteractPacket.TYPE, ServerboundEnderfaceInteractPacket.STREAM_CODEC,
            MachinePayloadHandler.Server.getInstance()::handleEnderfaceInteract);

        registrar.playToServer(ServerboundTransferItemsPacket.TYPE, ServerboundTransferItemsPacket.STREAM_CODEC,
            MachinePayloadHandler.Server.getInstance()::handleTransferItems);
    }

}
