package com.enderio.core.common.network;

import com.enderio.core.EnderCore;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CoreNetwork {
    private static final String PROTOCOL_VERSION = "1.0";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event
            .registrar(EnderCore.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.play(EmitParticlePacket.ID, EmitParticlePacket::new,
            handler -> handler
                .client(ClientPayloadHandler.getInstance()::handleEmitParticle));

        registrar.play(EmitParticlesPacket.ID, EmitParticlesPacket::new,
            handler -> handler
                .client(ClientPayloadHandler.getInstance()::handleEmitParticles));

        registrar.play(S2CDataSlotUpdate.ID, S2CDataSlotUpdate::new,
            handler -> handler
                .client(ClientPayloadHandler.getInstance()::handleDataSlotUpdate));

        registrar.play(C2SDataSlotChange.ID, C2SDataSlotChange::new,
            handler -> handler
                .server(ServerPayloadHandler.getInstance()::handleDataSlotChange));
    }
}
