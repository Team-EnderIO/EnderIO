package com.enderio.machines.common.init;

import com.enderio.core.EnderCore;
import com.enderio.core.common.network.CoreNetwork;
import com.enderio.machines.common.network.MachinePayloadHandler;
import com.enderio.machines.common.network.PoweredSpawnerSoulPacket;
import com.enderio.machines.common.network.SoulEngineSoulPacket;
import com.enderio.machines.common.network.UpdateCrafterTemplatePacket;
import com.enderio.machines.common.souldata.EngineSoul;
import com.enderio.machines.common.souldata.SpawnerSoul;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MachineNetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event
            .registrar(EnderCore.MODID)
            .versioned(PROTOCOL_VERSION);


        //Sync soul data (optional)
        SpawnerSoul.SPAWNER.subscribeAsSyncable(PoweredSpawnerSoulPacket::new);
        EngineSoul.ENGINE.subscribeAsSyncable(SoulEngineSoulPacket::new);

        registrar.play(PoweredSpawnerSoulPacket.ID, PoweredSpawnerSoulPacket::new,
            handler -> handler.client(MachinePayloadHandler.Client.getInstance()::handlePoweredSpawnerSoul));

        registrar.play(SoulEngineSoulPacket.ID, SoulEngineSoulPacket::new,
            handler -> handler.client(MachinePayloadHandler.Client.getInstance()::handleSoulEngineSoul));

        registrar.play(UpdateCrafterTemplatePacket.ID, UpdateCrafterTemplatePacket::new,
            handler -> handler.server(MachinePayloadHandler.Server.getInstance()::updateCrafterTemplate));
    }
}
