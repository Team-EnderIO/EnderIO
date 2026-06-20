package com.enderio.enderio.foundation.network;

import com.enderio.enderio.foundation.network.packets.ClientBoundRemoveCapacitorBankPacket;
import com.enderio.enderio.foundation.network.packets.ClientBoundSyncCapacitorBankPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundConduitExtraGuiDataPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundConduitListPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundFarmStationSoulPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundPoweredSpawnerSoulPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundSolarSoulPacket;
import com.enderio.enderio.foundation.network.packets.ClientboundSoulEngineSoulPacket;
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
import com.enderio.enderio.foundation.network.packets.ServerboundSetItemFilterSlotPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundSyncProbeStatePacket;
import com.enderio.enderio.foundation.network.packets.ServerboundTimerFilterPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundToggleMagnetPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundTransferItemsPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundUpdateCoordinateSelectionNameMenuPacket;
import com.enderio.enderio.foundation.network.packets.ServerboundUpdateCrafterTemplatePacket;
import com.enderio.enderio.foundation.network.packets.SetConduitConnectionConfigPacket;
import com.enderio.enderio.foundation.souldata.EngineSoul;
import com.enderio.enderio.foundation.souldata.FarmSoul;
import com.enderio.enderio.foundation.souldata.SolarSoul;
import com.enderio.enderio.foundation.souldata.SpawnerSoul;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class EIONetwork {
    private static final String PROTOCOL_VERSION = "3";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Sync soul data (optional)
        SpawnerSoul.SPAWNER.subscribeAsSyncable(ClientboundPoweredSpawnerSoulPacket::new);
        EngineSoul.ENGINE.subscribeAsSyncable(ClientboundSoulEngineSoulPacket::new);
        FarmSoul.FARM.subscribeAsSyncable(ClientboundFarmStationSoulPacket::new);
        SolarSoul.SOLAR.subscribeAsSyncable(ClientboundSolarSoulPacket::new);

        // TODO: Tidy up this class.
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(ClientboundSyncTravelDataPacket.TYPE, ClientboundSyncTravelDataPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleSyncTravelDataPacket);

        registrar.playToClient(ClientboundTravelTargetUpdatedPacket.TYPE, ClientboundTravelTargetUpdatedPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleAddTravelTarget);

        registrar.playToClient(ClientboundTravelTargetRemovedPacket.TYPE, ClientboundTravelTargetRemovedPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleRemoveTravelTarget);

        registrar.playToClient(ClientBoundSyncCapacitorBankPacket.TYPE, ClientBoundSyncCapacitorBankPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleSyncCapacitorBankPacket);

        registrar.playToClient(ClientBoundRemoveCapacitorBankPacket.TYPE, ClientBoundRemoveCapacitorBankPacket.STREAM_CODEC,
            ClientPayloadHandler.getInstance()::handleRemoveCapacitorBankPacket);

        registrar.playToServer(ServerboundUpdateCoordinateSelectionNameMenuPacket.TYPE, ServerboundUpdateCoordinateSelectionNameMenuPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleCoordinateSelectionName);

        registrar.playToServer(ServerboundRequestTravelPacket.TYPE, ServerboundRequestTravelPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleTravelRequest);

        registrar.playToServer(ServerboundRequestShortTravelPacket.TYPE, ServerboundRequestShortTravelPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleShortTravelRequest);

        registrar.playToServer(ServerboundSetFluidFilterSlotPacket.TYPE, ServerboundSetFluidFilterSlotPacket.STREAM_CODEC,
            ServerPayloadHandler.getInstance()::handleSetFluidFilterSlot);

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
            ConduitCommonPayloadHandler.getInstance()::handle);

        registrar.playToServer(ServerboundOpenConduitFilterMenu.TYPE, ServerboundOpenConduitFilterMenu.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handle);

        registrar.playToServer(ServerboundSyncProbeStatePacket.TYPE, ServerboundSyncProbeStatePacket.STREAM_CODEC,
            ConduitServerPayloadHandler.getInstance()::handle);

        registrar.playToClient(ClientboundPoweredSpawnerSoulPacket.TYPE, ClientboundPoweredSpawnerSoulPacket.STREAM_CODEC,
            MachinePayloadHandler.Client.getInstance()::handlePoweredSpawnerSoul);

        registrar.playToClient(ClientboundSoulEngineSoulPacket.TYPE, ClientboundSoulEngineSoulPacket.STREAM_CODEC,
            MachinePayloadHandler.Client.getInstance()::handleSoulEngineSoul);

        registrar.playToClient(ClientboundFarmStationSoulPacket.TYPE, ClientboundFarmStationSoulPacket.STREAM_CODEC,
            MachinePayloadHandler.Client.getInstance()::handleFarmingStationSoul);

        registrar.playToClient(ClientboundSolarSoulPacket.TYPE, ClientboundSolarSoulPacket.STREAM_CODEC,
            MachinePayloadHandler.Client.getInstance()::handleSolarSoul);

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
