package com.enderio.machines.common.init;

import com.enderio.machines.EnderIOMachines;
import com.enderio.machines.common.network.CycleIOConfigPacket;
import com.enderio.machines.common.network.EnderfaceInteractPacket;
import com.enderio.machines.common.network.FarmStationSoulPacket;
import com.enderio.machines.common.network.MachinePayloadHandler;
import com.enderio.machines.common.network.PoweredSpawnerSoulPacket;
import com.enderio.machines.common.network.SolarSoulPacket;
import com.enderio.machines.common.network.SoulEngineSoulPacket;
import com.enderio.machines.common.network.TransferItemsPacket;
import com.enderio.machines.common.network.UpdateCrafterTemplatePacket;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.FarmSoul;
import com.enderio.machines.common.souldata.SolarSoul;
import com.enderio.machines.common.souldata.SpawnerSoul;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EnderIOMachines.MODULE_MOD_ID)
public class MachineNetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        // Sync soul data (optional)
        SpawnerSoul.SPAWNER.subscribeAsSyncable(PoweredSpawnerSoulPacket::new);
        EngineSoul.ENGINE.subscribeAsSyncable(SoulEngineSoulPacket::new);
        FarmSoul.FARM.subscribeAsSyncable(FarmStationSoulPacket::new);
        SolarSoul.SOLAR.subscribeAsSyncable(SolarSoulPacket::new);

        registrar.playToClient(PoweredSpawnerSoulPacket.TYPE, PoweredSpawnerSoulPacket.STREAM_CODEC,
                MachinePayloadHandler.Client.getInstance()::handlePoweredSpawnerSoul);

        registrar.playToClient(SoulEngineSoulPacket.TYPE, SoulEngineSoulPacket.STREAM_CODEC,
                MachinePayloadHandler.Client.getInstance()::handleSoulEngineSoul);

        registrar.playToClient(FarmStationSoulPacket.TYPE, FarmStationSoulPacket.STREAM_CODEC,
                MachinePayloadHandler.Client.getInstance()::handleFarmingStationSoul);
        registrar.playToClient(SolarSoulPacket.TYPE, SolarSoulPacket.STREAM_CODEC,
                MachinePayloadHandler.Client.getInstance()::handleSolarSoul);

        registrar.playToServer(UpdateCrafterTemplatePacket.TYPE, UpdateCrafterTemplatePacket.STREAM_CODEC,
                MachinePayloadHandler.Server.getInstance()::updateCrafterTemplate);

        registrar.playToServer(CycleIOConfigPacket.TYPE, CycleIOConfigPacket.STREAM_CODEC,
                MachinePayloadHandler.Server.getInstance()::handleCycleIOConfigPacket);

        registrar.playToServer(EnderfaceInteractPacket.TYPE, EnderfaceInteractPacket.STREAM_CODEC,
                MachinePayloadHandler.Server.getInstance()::handleEnderfaceInteract);

        registrar.playToServer(TransferItemsPacket.TYPE, TransferItemsPacket.STREAM_CODEC,
            MachinePayloadHandler.Server.getInstance()::handleTransferItems);
    }
}
